
#############################################################
# Create edx set, validation set, and submission file
#############################################################

# Note: this process could take a couple of minutes

if(!require(tidyverse)) install.packages("tidyverse", repos = "http://cran.us.r-project.org")
if(!require(caret)) install.packages("caret", repos = "http://cran.us.r-project.org")

# MovieLens 10M dataset:
# https://grouplens.org/datasets/movielens/10m/
# http://files.grouplens.org/datasets/movielens/ml-10m.zip

dl <- tempfile()
download.file("http://files.grouplens.org/datasets/movielens/ml-10m.zip", dl)

ratings <- read.table(text = gsub("::", "\t", readLines(unzip(dl, "ml-10M100K/ratings.dat"))),
                      col.names = c("userId", "movieId", "rating", "timestamp"))

movies <- str_split_fixed(readLines(unzip(dl, "ml-10M100K/movies.dat")), "\\::", 3)
colnames(movies) <- c("movieId", "title", "genres")
movies <- as.data.frame(movies) %>% mutate(movieId = as.numeric(levels(movieId))[movieId],
                                           title = as.character(title),
                                           genres = as.character(genres))

movielens <- left_join(ratings, movies, by = "movieId")

# Analyze the dataset and find how many movies and users

movielens[1:6,] %>% select(userId,movieId,rating,title)
# head(movielens)

movielens %>%
  summarize(n_users = n_distinct(userId),
            n_movies = n_distinct(movieId))

# Validation set will be 10% of MovieLens data

set.seed(1)
test_index <- createDataPartition(y = movielens$rating, times = 1, p = 0.1, list = FALSE)
edx <- movielens[-test_index,]
temp <- movielens[test_index,]

# Make sure userId and movieId in validation set are also in edx set

validation <- temp %>% 
  semi_join(edx, by = "movieId") %>%
  semi_join(edx, by = "userId")

# Add rows removed from validation set back into edx set

removed <- anti_join(temp, validation)
edx <- rbind(edx, removed)

rm(dl, ratings, movies, test_index, temp, movielens, removed)


# Data Visualization and understanding more from the dataset 

# Compute the average and standard error for each category of the movies,
# and use the analysis to understand more from the data

movielens %>% group_by(genres) %>%
  summarize(n = n(), avg = mean(rating), se = sd(rating)/sqrt(n())) %>%
  filter(n >= 1000) %>% 
  mutate(genres = reorder(genres, avg)) %>%
  ggplot(aes(x = genres, y = avg, ymin = avg - 2*se, ymax = avg + 2*se)) + 
  geom_point() +
  geom_errorbar() + 
  theme(axis.text.x = element_text(angle = 90, hjust = 1))

# Analyze the highest median number of ratings visualizing by year

movielens %>% group_by(movieId) %>%
  summarize(n = n(), year = as.character(first(year))) %>%
  qplot(year, n, data = ., geom = "boxplot") +
  coord_trans(y = "sqrt") +
  theme(axis.text.x = element_text(angle = 90, hjust = 1))

# Movie-rating distribution

movielens %>% 
  dplyr::count(movieId) %>% 
  ggplot(aes(n)) + 
  geom_histogram(bins = 30, color = "black") + 
  scale_x_log10() + 
  ggtitle("Movies")


# Create a function that computes the RMSE for vectors of ratings 
# and their corresponding predictors

RMSE <- function(true_ratings, predicted_ratings){
  sqrt(mean((true_ratings - predicted_ratings)^2))
}


# Modeling movie effects

r_mu <- mean(edx$rating)

avgs_movie <- edx %>%
  group_by(movieId) %>%
  summarize(b_i = mean(rating - r_mu))

#Movie effective model

predicted_ratingx <- r_mu + validation %>%
  left_join(avgs_movie, by = "movieId") %>%
  .$b_i

# We can see the estimates using with the model vary substantially

avgs_movie %>% 
  qplot(b_i, geom ="histogram", bins = 10, data = ., color = I("black"))


# RMSE on the first model

model_1x_rmse <- RMSE(validation$rating, predicted_ratingx)
rmse_results <- data_frame(method="Effective Model",
                           RMSE = model_1x_rmse)
rmse_results %>% knitr::kable()


# Some users are more active than others at rating movies

movielens %>% 
  dplyr::count(userId) %>% 
  ggplot(aes(n)) + 
  geom_histogram(bins = 30, color = "black") + 
  scale_x_log10() + 
  ggtitle("Users")

# We should include the part as building the recommendation-sys

avgs_user <- edx %>%
  left_join(avgs_movie, by="movieId") %>%
  group_by(userId) %>%
  summarize(b_u = mean(rating - r_mu - b_i))

# Compute the average rating for those users 
# that have rated over 100 movies

edx %>% 
  group_by(userId) %>% 
  summarize(b_u = mean(rating)) %>% 
  filter(n()>=100) %>%
  ggplot(aes(b_u)) + 
  geom_histogram(bins = 30, color = "black")


# We can now build the second model and see how much the RMSE improved

predicted_ratings <- validation %>%
  left_join(avgs_movie, by="movieId") %>%
  left_join(avgs_user, by="userId") %>%
  mutate(pred = r_mu + b_i + b_u) %>%
  .$pred

# RMSE on the final model

model_optm <- RMSE(validation$rating, predicted_ratings)
rmse_results <- bind_rows(rmse_results,
                          data_frame(method="Final Effective Model",
                                     RMSE = model_optm))

rmse_results %>% knitr::kable()


##-------Thank you----------


 

