
library(tidyverse)
library(caret)
library(matrixStats)
library(vcd)
library(scales)
library(ggthemes)
library(knitr)
library(ggplot2)
library(plyr)
library(gridExtra)
library(gmodels)
library(grid)
library(data.table)

#############################################################
# Create edx set, validation set, and submission file
#############################################################


# if(!require(tidyverse)) install.packages("tidyverse", repos = "http://cran.us.r-project.org")
# if(!require(caret)) install.packages("caret", repos = "http://cran.us.r-project.org")

# MovieLens 10M dataset:
# https://grouplens.org/datasets/movielens/10m/
# http://files.grouplens.org/datasets/movielens/ml-10m.zip


temp <- tempfile()
download.file("https://www.kaggle.com/uciml/adult-census-income/downloads/adult-census-income.zip/3",temp, mode="wb")
unzip(temp, "adult.csv")

# mark the missing value as NA
incomeData <- read.table("adult.csv",
                       sep = ",", skip=0,
                       header = TRUE, 
                       na.strings = "?")
            

# dataset rows & columns
dim(incomeData)

# for convenient, we rename some columns
setnames(incomeData, old=c("education.num","marital.status","capital.gain","capital.loss","hours.per.week","native.country"), new=c("education_num","marital_status","capital_gain","capital_loss","hours_per_week","native_country"))

# view the first 3-rows of the dataframe
incomeData[0:3,]

# the dataframe structure
str(incomeData, vec.len = 5, strict.width = "no", width = 30)

# drop NA
incomeData <- na.omit(incomeData)

# re-enumerate the rows
row.names(incomeData) <- 1:nrow(incomeData)
print(dim(incomeData)[1])
incomeData[0:3,]

summary(incomeData$hours_per_week)

incomeData$incomeN1 <- incomeData$income
levels(incomeData$incomeN1) <- c(0,1)
incomeData$incomeN2 <- as.numeric(levels(incomeData$incomeN1))[incomeData$incomeN1]

cor(incomeData$hours_per_week, incomeData$incomeN2, method = "pearson")

incomeData$dif_hours[incomeData$hours_per_week < 40] <- "less_than_40h"
incomeData$dif_hours[incomeData$hours_per_week >= 40 & incomeData$hours_per_week <= 45] <- "between_40h_and_45h"
incomeData$dif_hours[incomeData$hours_per_week > 45 & incomeData$hours_per_week <= 60  ] <- "between_45h_and_60h"
incomeData$dif_hours[incomeData$hours_per_week > 60 & incomeData$hours_per_week <= 80  ] <- "between_60h_and_80h"
incomeData$dif_hours[incomeData$hours_per_week > 80] <- "more_than_80h"

incomeData$dif_hours <- factor(incomeData$dif_hours, ordered = FALSE, 
                               levels = c("less_than_40h", "between_40h_and_45h","between_45h_and_60h",
                                      "between_60h_and_80h","more_than_80h"))

# summarize the derived factor variable
summary(incomeData$dif_hours)

# percentages of each level
for(i in 1:length(summary(incomeData$dif_hours))){    
   print(round(100*summary(incomeData$dif_hours)[i]/sum(!is.na(incomeData$dif_hours)), 2)) }

levels(incomeData$native_country)[36:41]

America <- c("Cuba", "Guatemala", "Jamaica", "Nicaragua", "Puerto-Rico",  "Dominican-Republic", "El-Salvador", 
                     "Haiti", "Honduras", "Mexico", "Trinadad&Tobago", "Ecuador", "Peru", "Columbia")
Asia <- c("Cambodia", "China", "Hong", "Laos", "Thailand", "Japan", "Taiwan", "Vietnam", "India", "Iran")
Europe <- c("England", "Germany", "Holand-Netherlands", "Ireland", 
                   "France", "Greece", "Italy", "Portugal", "Scotland", "Poland", "Yugoslavia", "Hungary")

incomeData <- mutate(incomeData, native_region = ifelse(native_country %in% Asia, "Asia",                                           
                                                 ifelse(native_country %in% America, "America",
                                                 ifelse(native_country %in% Europe, "Europe",
                                                 ifelse(native_country == "United-States", "United-States", 
                                                                          "Outlying-US" )))))

incomeData$native_region <- factor(incomeData$native_region, ordered = FALSE)
incomeData[0:3,]

summary(incomeData$capital_gain)
summary(incomeData$capital_loss)

# the mean values on both variables
avg_gain <- mean(incomeData$capital_gain)
avg_loss <- mean(incomeData$capital_loss)
kable(data.frame(AVG_Capital_Gain = avg_gain, AVG_Capital_Loss = avg_loss), 
      caption = "Zero Values Included of AVG Capital")

# the mean without zero values
avg_gain <- mean(subset(incomeData$capital_gain, incomeData$capital_gain > 0))
avg_loss <- mean(subset(incomeData$capital_loss, incomeData$capital_loss > 0))
kable(data.frame(AVG_Capital_Gain = avg_gain, AVG_Capital_Loss = avg_loss), caption = "NonZero AVG Capital Values")

# We list the IQR of the nonzero values on both and use them as reference when derived new factor variables
interquartile_gain <- IQR(subset(incomeData$capital_gain, incomeData$capital_gain > 0))
interquartile_loss <- IQR(subset(incomeData$capital_loss, incomeData$capital_loss > 0))
gain_quartile <- quantile(x = subset(incomeData$capital_gain, incomeData$capital_gain > 0), probs = seq(0, 1, 0.25))
loss_quartile <- quantile(x = subset(incomeData$capital_loss, incomeData$capital_loss > 0), probs = seq(0, 1, 0.25))
kable(x = data.frame(Capital_Gain = gain_quartile, Capital_Loss = loss_quartile), caption = "Nonzero Capital Quartiles")

income_dat <- mutate(incomeData,                      
                     cap_gain = ifelse(incomeData$capital_gain < 3600, " Low",
                                       ifelse(incomeData$capital_gain >= 3600 & 
                                              incomeData$capital_gain <= 15000, " Medium", " High")))
income_dat$cap_gain <- factor(income_dat$cap_gain,ordered = TRUE,levels = c(" Low", " Medium", " High"))

income_dat <- mutate(income_dat, 
                     cap_loss = ifelse(income_dat$capital_loss < 1500, " Low",
                                       ifelse(income_dat$capital_loss >= 1500 & 
                                              income_dat$capital_loss <= 2000, " Medium", " High")))
income_dat$cap_loss <- factor(income_dat$cap_loss,ordered = TRUE,levels = c(" Low", " Medium", " High"))

# For example:
cor(income_dat$hours_per_week, income_dat$incomeN2, method = "pearson")
cor(income_dat$age, income_dat$incomeN2, method = "pearson")
cor(income_dat$fnlwgt, income_dat$incomeN2, method = "pearson")

ggplot(mapping = aes(x = income, y = capital_gain),
       data = subset(income_dat, income_dat$capital_gain > 0)) + 
  geom_boxplot() +
  stat_summary(fun.y = mean,geom = 'point',shape = 19,color = "orange",cex = 3.5) +
  coord_cartesian(ylim = c(0, 28500)) +
  scale_y_continuous(breaks = seq(0, 28500, 1250)) +
  labs(x = "Income Levels", y = "Capital Gain") +
  ggtitle("Nonzero Capital Gain by Income") 

# Bar plot

gain_lg <- lapply(X = levels(income_dat$income), FUN = function(v){  
  df <- subset(income_dat, income_dat$income == v)     
  df <- within(df, cap_gain <- factor(cap_gain, levels = names(sort(table(cap_gain), 
                                                          decreasing = FALSE))))
    
  ggplot(data = df, aes(x = cap_gain, fill = cap_gain)) + 
    geom_bar(aes(y = (..count..)/sum(..count..))) +
    coord_flip() + 
    theme(legend.position = "top") +
    geom_text(aes(label = scales::percent((..count..)/sum(..count..)),
                y = (..count..)/sum(..count..) ), stat = "count", vjust = -.1) +
    labs(x = "Capital Gain", y = "", fill = "Capital Gain") +
    ggtitle(paste("Income", v, sep = "")) +  
    scale_y_continuous(labels = percent) })

grid.arrange(grobs = gain_lg, ncol = 2)

# Bar plot of cap_loss (by income)

loss_lg <- lapply(X = levels(income_dat$income), FUN = function(v){  
  df <- subset(income_dat, income_dat$income == v)     
  df <- within(df, cap_loss <- factor(cap_loss, levels = names(sort(table(cap_loss), 
                                                          decreasing = FALSE))))
    
  ggplot(data = df, aes(x = cap_loss, fill = cap_loss)) + 
    geom_bar(aes(y = (..count..)/sum(..count..))) +
    coord_flip() + 
    theme(legend.position = "top") +
    geom_text(aes(label = scales::percent((..count..)/sum(..count..)),
                y = (..count..)/sum(..count..) ), stat = "count", vjust = -.1) +
    labs(x = "Capital Loss", y = "", fill = "Capital Loss") +
    ggtitle(paste("Income", v, sep = "")) +  
    scale_y_continuous(labels = percent) })

grid.arrange(grobs = loss_lg, ncol = 2)

summary(income_dat$age)

# We can see the bulk of individuals are from 20 to 50
qplot(x = income_dat$age, data = income_dat, binwidth = 5, 
      color = I('lightgreen'), fill = I('#2587f2'), xlab = "Age", ylab = "Count",
      main = "Age in Histogram") +
  scale_x_continuous(breaks = seq(0, 100, 5)) +   
  scale_y_continuous(breaks = seq(0, 5000, 450))

# empirical density chart
with(income_dat, qplot(age, fill=income, geom="density", position="fill")) 

ggplot(data = income_dat, mapping = aes(x = age)) + 
  geom_histogram(binwidth = 5, color = "lightgreen", fill = "blue", alpha = 0.8) +
  coord_flip() + 
  theme(legend.position = "top") +
  scale_x_continuous(breaks = seq(0, 100, 5)) + 
  facet_wrap(~income) +
  ggtitle("Income") 

summary(subset(income_dat$age, income_dat$income == "<=50K"))
summary(subset(income_dat$age, income_dat$income == ">50K"))

cdplot(income_dat$income ~ income_dat$age, bw = 1.5,
        xlab = "Age",
        ylab = "Income Levels",
        colours = "darkblue",
        main = "Income vs Age in Density")

summary(income_dat$hours_per_week)

summary(subset(income_dat$hours_per_week, income_dat$income == "<=50K"))
summary(subset(income_dat$hours_per_week, income_dat$income == ">50K"))

hpw_lg <- lapply(levels(income_dat$income), function(v){    
    df <- subset(income_dat, income_dat$income == v)     
    df <- within(df, dif_hours <- factor(dif_hours, levels = names(sort(table(dif_hours), 
                                                           decreasing = FALSE))))
  
    ggplot(data = df, aes(x = dif_hours, fill = dif_hours)) + 
      geom_bar(aes(y = (..count..)/sum(..count..))) +
      coord_flip() + 
      theme(legend.position = "top") +

      geom_text(aes(label = scales::percent((..count..)/sum(..count..)),
                    y = (..count..)/sum(..count..) ), stat = "count", vjust = -.1, size = 3) +
      labs(x = "Hours per week", y = "", fill = "Hours per week") +
    theme(legend.position = "", axis.text.y = element_text(angle = 45, hjust = 1)) +
      ggtitle(paste("Income Levels ", v, sep="")) + 
      scale_y_continuous(labels = percent) })

grid.arrange(grobs = hpw_lg, ncol = 2)

cor(income_dat$education_num, income_dat$incomeN2, method = "pearson")

summary(income_dat$education)

income_dat$education <- factor(income_dat$education, levels = names(sort(table(income_dat$education),   
                                                                               decreasing = FALSE)))

ggplot(income_dat, aes(x = income_dat$education, fill = income_dat$education)) + 
  geom_bar(aes(y = (..count..)/sum(..count..))) +
  coord_flip() + 
  theme(legend.position = "top") +
  geom_text(aes(label = scales::percent((..count..)/sum(..count..)), y = (..count..)/sum(..count..) ), 
            stat = "count", vjust = -.1, size = 3.5) +
  labs(x = "Education Levels", y = "", fill = "Education Levels") +
  theme(legend.position = "", axis.text.y = element_text(angle = 45, hjust = 1)) +
  scale_y_continuous(labels = percent)

nrow(subset(income_dat, income_dat$education == "Preschool" &
                        income_dat$income == ">50K" ))

new_edu <- levels(income_dat$education)
new_edu <- new_edu[!is.element(new_edu, "Preschool")]

edu_mod_lg <- lapply(new_edu, function(v){
  ggplot(data = subset(income_dat, income_dat$education == v), aes(x = subset(income_dat, income_dat$education == v)$income, 
             fill = subset(income_dat, income_dat$education == v)$income)) +
    geom_bar(aes(y = (..count..)/sum(..count..))) +
    coord_flip() + 
    theme(legend.position = "top") +
    geom_text(aes(label = scales::percent((..count..)/sum(..count..)), y = (..count..)/sum(..count..)), 
             stat = "count", vjust =  c(2, 0.5), size = 3) +
    labs(x = "Income", y = "", fill = "Income") +
    ggtitle(v) +  
    theme(legend.position = "", plot.title = element_text(size = 11, face = "bold")) +    
    scale_y_continuous(labels = percent) })

grid.arrange(grobs = edu_mod_lg[3:14], ncol = 2)

gender_income <- lapply(levels(income_dat$sex), function(v){
   ggplot(data = subset(income_dat, income_dat$sex == v), aes(x = subset(income_dat, income_dat$sex == v)$income, 
             fill = subset(income_dat, income_dat$sex == v)$income))+
  geom_bar(aes(y = (..count..)/sum(..count..))) +
  coord_flip() + 
  theme(legend.position = "top") +
  geom_text(aes(label = scales::percent((..count..)/sum(..count..)), y = (..count..)/sum(..count..)), 
            stat = "count", vjust = -0.1, size = 3) +
  labs(x = "Income Levels", y = "", fill = "Income levels") +
  ggtitle(paste(v)) +  
  theme(legend.position = "", plot.title = element_text(size = 11, face = "bold"),
        axis.text.y = element_text(hjust = 1)) +     
  scale_y_continuous(labels = percent) })

grid.arrange(grobs = gender_income, ncol = 2)

# Proportion of men and women with income <50K and >=50K:
prop.table(table(income_dat$sex, income_dat$income), margin = 1)

lg_gender_edu <- lapply(levels(income_dat$sex), function(v){    
    df <- subset(income_dat, income_dat$sex == v)      
    df <- within(df, education <- factor(education, levels = names(sort(table(education), 
                                                                   decreasing = FALSE))))
       
   ggplot(data = df, aes(x = df$education, fill = subset(income_dat, income_dat$sex == v)$education))+
     geom_bar(aes(y = (..count..)/sum(..count..))) +
     geom_text(aes(label = scales::percent((..count..)/sum(..count..)), y = (..count..)/sum(..count..)), 
               stat = "count", vjust = -0.2, size = 3) +
     labs(x = "Edu Levels", y = "", fill = "Edu Levels") +
     ggtitle(paste(v)) +  
     theme(legend.position = "", plot.title = element_text(size = 11, face = "bold"),
           axis.text.x = element_text(angle = 45, hjust = 1)) +     
     scale_y_continuous(labels = percent) })

grid.arrange(grobs = lg_gender_edu, ncol = 2)

drops <- c("workclass","education","occupation","relationship","race","native_country","incomeN1","incomeN2")
income_dat <- income_dat[, !(names(income_dat) %in% drops)]
income_dat[0:3,]

#ifelse(income_dat$income == "somevalue",0,1)
levels(income_dat$income) <- c(0,1)
income_dat$income2 <- as.numeric(levels(income_dat$income))[income_dat$income]

income_dat$marital_status <- as.factor(income_dat$marital_status)
income_dat$marital_status2 <- as.numeric(income_dat$marital_status)

income_dat$sex <- as.factor(income_dat$sex)
income_dat$sex2 <- as.numeric(income_dat$sex)

income_dat$dif_hours <- as.factor(income_dat$dif_hours)
income_dat$dif_hours2 <- as.numeric(income_dat$dif_hours)

income_dat$native_region <- as.factor(income_dat$native_region)
income_dat$native_region2 <- as.numeric(income_dat$native_region)

income_dat$cap_gain <- as.factor(income_dat$cap_gain)
income_dat$cap_gain2 <- as.numeric(income_dat$cap_gain)

income_dat$cap_loss <- as.factor(income_dat$cap_loss)
income_dat$cap_loss2 <- as.numeric(income_dat$cap_loss)

income_dat <- income_dat %>% select("age","fnlwgt","education_num","marital_status2","sex2","capital_gain","capital_loss",
                                   "hours_per_week","dif_hours2","native_region2","cap_gain2","cap_loss2","income2")
setnames(income_dat, old = c("marital_status2","sex2","dif_hours2","native_region2","cap_gain2","cap_loss2","income2"),
        new = c("marital_status","sex","dif_hours","native_region","cap_gain","cap_loss","income"))
income_dat[0:3,]

# Review the new structure
summary(income_dat)

dim(income_dat)

# Validation set will be 25% of new income data

set.seed(1)
test_index <- createDataPartition(y = income_dat$income, times = 1, p = 0.25, list = FALSE)
edx <- income_dat[-test_index,]
validation <- income_dat[test_index,]


# print the row numbers of train-set & test-set

trainRows <- dim(edx)[1]
testRows <- dim(validation)[1]
print(trainRows)
print(testRows)

library(rpart)

edx_tree <- rpart(income ~ ., 
                  
                     data = edx)
plot(edx_tree)
text(edx_tree)


edx %>% 
  mutate(income_hat = predict(edx_tree)) %>% 
  ggplot() +
  geom_point(aes(age+fnlwgt+education_num+marital_status+sex+capital_gain+capital_loss+hours_per_week+dif_hours+native_region+cap_gain+cap_loss, income)) +

  geom_step(aes(age+fnlwgt+education_num+marital_status+sex+capital_gain+capital_loss+hours_per_week+dif_hours+native_region+cap_gain+cap_loss, income_hat), col=2)  


set.seed(1)
fit_rpart <- train(income ~ .,
                  method="rpart",
                  tuneGrid = data.frame(cp=seq(0.0,0.1,len=16)),
                  data = edx)
ggplot(fit_rpart)                

RMSE <- function(true_ratings, predicted_ratings){
  sqrt(mean((true_ratings - predicted_ratings)^2))
}

d_tree_rmse <- RMSE(validation$income, predict(fit_rpart))
rmse_results <- data_frame(method="D-Tree Model",
                           RMSE = d_tree_rmse)
rmse_results %>% knitr::kable()

library(randomForest)
library(Rborist)

train_rf <- randomForest(income ~ ., data = edx)
plot(train_rf)

fit_rf <- train(income ~ .,
             method="Rborist",
             tuneGrid=data.frame(predFixed=5,
                                 minNode = seq(3,45)),
             data = edx)  
ggplot(fit_rf)


rf_rmse <- RMSE(validation$income, predict(fit_rf))
rmse_results <- bind_rows(rmse_results,
                          data_frame(method="Random Forest Model",
                           RMSE = rf_rmse))
rmse_results %>% knitr::kable()

control <- trainControl(method="cv",number=8,p=0.8)

grid <- expand.grid(minNode=c(1,35),predFixed=c(2))

fit_gbm <- train(income ~ .,
                 data = edx,
                 method = "gbm",
                 metric = "RMSE",
                 trControl = control,
                 verbose = FALSE)
                 
ggplot(fit_gbm)

gbm_rmse <- RMSE(validation$income, predict(fit_gbm))
rmse_results <- bind_rows(rmse_results,
                          data_frame(method="Gradient Boosting Model",
                           RMSE = gbm_rmse))
rmse_results %>% knitr::kable()

control <- trainControl(method = "cv",number = 9, p=.85)

fit_knn <- train(income ~ .,
                 data=edx,
                 method = "knn",
                 tuneGrid = data.frame(k=c(1,3,5,7,9)),
                 trControl = control)
ggplot(fit_knn)

knn_rmse <- RMSE(validation$income, predict(fit_knn))
rmse_results <- bind_rows(rmse_results,
                          data_frame(method="K-NN Model",
                           RMSE = knn_rmse))
rmse_results %>% knitr::kable()

# mainly consider the variables with Causal-Relationship
covariates_new <- paste("age","education_num","sex","dif_hours","cap_gain","cap_loss", sep = "+")
form_new <- as.formula(paste("income ~", covariates_new))

start_time <- proc.time()
glm_model <- glm(formula = form_new, data = edx, family = binomial(link = "logit"),
                     x = TRUE,y = TRUE)

car::vif(glm_model)

predicted_probs <- predict(glm_model, type="response")
glm_rmse <- RMSE(validation$income, predicted_probs)
rmse_results <- bind_rows(rmse_results,data_frame(method="GLM Model",RMSE = glm_rmse))
rmse_results %>% knitr::kable()

observed_values <- ifelse(edx$income  == " >50K", 1, 0)
predicted_response <- ifelse(predicted_probs > 0.5, 1, 0)

mean(observed_values == predicted_response)

summary(glm_model)

## Accuracy:
mean(predicted_response == edx$income)
