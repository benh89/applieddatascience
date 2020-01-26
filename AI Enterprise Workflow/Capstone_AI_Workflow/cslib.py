#!/usr/bin/env python
"""
functions for Data Ingestion, EDA
"""

import os
import sys
import re
import shutil
import time
import pickle
from datetime import datetime
import numpy as np
import pandas as pd
import matplotlib.pyplot as plt
import matplotlib.dates as mdates
register_matplotlib_converters()

## import the following libraries
import os
import pandas as pd
import numpy as np
import sqlite3

## specify the directory you saved the data in
data_dir = os.path.join("..","data")

# COLORS = ["darkorange","royalblue","slategrey"]


def fetch_db_data(file_path1):
    """
    laod the data from the database into a dataframe
    """   
    ## make a connection to the database
    rdb_path = os.path.join(".",file_path1)
    conn = sqlite3.connect(rdb_path)

    ## print the table names
    tables = [t[0] for t in conn.execute("SELECT name FROM sqlite_master WHERE     type='table';")]
    print("pre-view tables:", tables)
    
    # write a query first
    query = """
    SELECT cu.customer_id, cu.last_name, cu.first_name, cu.DOB, cu.city, cu.state, co.country_name, cu.gender
    FROM CUSTOMER cu
    INNER JOIN COUNTRY co
    ON cu.country_id = co.country_id;
    """
    _data = [d for d in conn.execute(query)]
    columns = ["customer_id","last_name","first_name","DOB","city","state","country","gender"]
    df_db = pd.DataFrame(_data,columns=columns)
    # df_db.head()
    duplicate_rows = df_db.duplicated()
    if True in duplicate_rows:
        df_db = df_db[~duplicate_rows]
    
    # A dataset was formed
    return df_db


def fetch_csv_churn(file_path2):
    """
    laod the data from a csv file into a dataframe
    """
    df_streams = pd.read_csv(os.path.join(".",file_path2))
    # print(df_streams.shape)
    df_streams.head(4)
    
    missing_stream_ids = np.isnan(df_streams['stream_id'])    
    if True in missing_stream_ids:
        df_streams = df_streams[~missing_stream_ids]
    
    # retrieve the data and form a small churned table
    customer_ids = df_streams['customer_id'].values

    #unique_ids = np.unique(df_streams['customer_id'].values)
    unique_ids = df_streams.customer_id.unique()

    streams = df_streams['subscription_stopped'].values
    has_churned = [0 if streams[customer_ids==uid].max() > 0 else 1 for uid in unique_ids]
    df_churn = pd.DataFrame({"customer_id": unique_ids,"is_subscriber": has_churned})

    #df_churn.head(4)   
    return df_churn
    

def feature_engineering_stream(file_path2):
    """
    form a small table as the 1st part of features engineering
    """
    df_streams = pd.read_csv(os.path.join(".",file_path2))
    # print(df_streams.shape)
    # df_streams.head(4)
    
    missing_stream_ids = np.isnan(df_streams['stream_id'])    
    if True in missing_stream_ids:
        df_streams = df_streams[~missing_stream_ids]
    
    ## we form a new stream table
    new_stream = df_streams[['customer_id','invoice_item_id','subscription_stopped']]\
                .groupby(['customer_id','invoice_item_id'],as_index=False).count()
    
    #new_stream.head(4)
    return new_stream
    

def sub_type():
    """
    """
    file_path = "Data/aavail-customers.db"
    rdb_path = os.path.join(".",file_path)
    conn = sqlite3.connect(rdb_path)

    ## print the table names
    tables = [t[0] for t in conn.execute("SELECT name FROM sqlite_master WHERE type='table';")]
    # print(tables)
    
    ## retrieve data from "invoice item" table
    invo_item = """
    SELECT invoice_item_id, invoice_item as subscriber_type FROM INVOICE_ITEM;
    """
    _dat = [d for d in conn.execute(invo_item)]
    columns = ["invoice_item_id","subscriber_type"]
    sub_type = pd.DataFrame(_dat,columns=columns)

    # sub_type.head(3)
    return sub_type


def feature_engineering(df_db, df_churn, new_stream, sub_type, training=False):
    """
    In order to meet our research goal, let's join the four tables together
    and do features engineering
    """

    new_dat1 = pd.merge(df_churn,df_db,how="inner",left_on='customer_id',right_on='customer_id')
    new_dat2 = pd.merge(new_dat1,new_stream,how="inner",left_on='customer_id',right_on='customer_id')
    df_new = pd.merge(new_dat2,sub_type,how="inner",left_on="invoice_item_id",right_on="invoice_item_id")

    ## finalizing the dataset:
    ## convert age to days
    df_new['age'] = np.datetime64('today') - pd.to_datetime(df_new['DOB'])
    ## convert to age(int) 
    df_new['age'] = [a.astype('timedelta64[Y]').astype(int) for a in df_new['age'].values]

    df_new['customer_name'] = df_new['first_name'] + " " + df_new['last_name']
    df_new['num_streams'] = df_new['subscription_stopped']
    new_col = ["country","state","customer_id","is_subscriber","customer_name","age","gender","subscriber_type","num_streams"]
    dat = df_new[new_col]
    dat = dat.sort_values(by=['customer_id'])

    ## reset index
    dat = dat.reset_index()
    dat.drop('index', inplace=True, axis=1)

    ## display
    dat.head()
    return dat



if __name__ == "__main__":

    run_start = time.time() 
    data_dir = os.path.join(".","data")
    print("...fetching data")

    ts_all = feature_engineering(df_db, df_churn, new_stream, sub_type, training=False)

    m, s = divmod(time.time()-run_start,60)
    h, m = divmod(m, 60)
    print("load time:", "%d:%02d:%02d"%(h, m, s))

    for key,item in ts_all.items():
        print(key,item.shape)
