create database omg;

show databases ;

USE omg;

show tables ;

-- Table Definitions with Auto-Increment and Cascade

-- Users Table
CREATE TABLE `users` (
                         `id` BIGINT NOT NULL AUTO_INCREMENT,
                         `username` VARCHAR(100) NOT NULL,
                         `password` VARCHAR(100) NOT NULL,
                         `name` VARCHAR(50) NOT NULL,
                         `nickname` VARCHAR(50) NOT NULL,
                         `phone_number` VARCHAR(50) NOT NULL,
                         `birthdate` DATE NOT NULL,
                         `gender` VARCHAR(10) NOT NULL,
                         `created_at` TIMESTAMP NOT NULL,
                         `updated_at` TIMESTAMP NULL,
                         `filename` VARCHAR(50) NULL,
                         `filepath` VARCHAR(50) NULL,
                         PRIMARY KEY (`id`)
);

-- Cities Table
CREATE TABLE `cities` (
                          `id` BIGINT NOT NULL AUTO_INCREMENT,
                          `name` VARCHAR(10) NOT NULL,
                          PRIMARY KEY (`id`)
);

-- Trips Table
CREATE TABLE `trips` (
                         `id` BIGINT NOT NULL AUTO_INCREMENT,
                         `city_id` BIGINT NOT NULL,
                         `trip_name` VARCHAR(100) NOT NULL,
                         `start_date` TIMESTAMP NOT NULL,
                         `end_date` TIMESTAMP NOT NULL,
                         `created_at` TIMESTAMP NOT NULL,
                         `updated_at` TIMESTAMP NULL,
                         PRIMARY KEY (`id`),
                         FOREIGN KEY (`city_id`) REFERENCES `cities` (`id`) ON DELETE CASCADE
);

-- Trip Dates Table
CREATE TABLE `trip_dates` (
                              `id` BIGINT NOT NULL AUTO_INCREMENT,
                              `trip_id` BIGINT NOT NULL,
                              `trip_date` DATE NOT NULL,
                              `day` INT NOT NULL,
                              PRIMARY KEY (`id`),
                              FOREIGN KEY (`trip_id`) REFERENCES `trips` (`id`) ON DELETE CASCADE
);

-- Trip Locations Table
CREATE TABLE `trip_locations` (
                                  `id` BIGINT NOT NULL AUTO_INCREMENT,
                                  `trip_date_id` BIGINT NOT NULL,
                                  `place_name` VARCHAR(100) NOT NULL,
                                  `latitude` DECIMAL(9,6) NOT NULL,
                                  `longitude` DECIMAL(9,6) NOT NULL,
                                  PRIMARY KEY (`id`),
                                  FOREIGN KEY (`trip_date_id`) REFERENCES `trip_dates` (`id`) ON DELETE CASCADE
);

-- Chat Rooms Table
CREATE TABLE `chat_rooms` (
                              `id` BIGINT NOT NULL AUTO_INCREMENT,
                              PRIMARY KEY (`id`)
);

-- Groups Table
CREATE TABLE `groups` (
                          `id` BIGINT NOT NULL AUTO_INCREMENT,
                          `chat_room_id` BIGINT NOT NULL,
                          `trip_id` BIGINT NOT NULL,
                          `invite_code` VARCHAR(50) NOT NULL,
                          `leader_id` BIGINT NOT NULL,
                          PRIMARY KEY (`id`),
                          FOREIGN KEY (`chat_room_id`) REFERENCES `chat_rooms` (`id`) ON DELETE CASCADE,
                          FOREIGN KEY (`trip_id`) REFERENCES `trips` (`id`) ON DELETE CASCADE,
                          FOREIGN KEY (`leader_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
);

-- Users Groups Table
CREATE TABLE `users_groups` (
                                `user_id` BIGINT NOT NULL,
                                `group_id` BIGINT NOT NULL,
                                PRIMARY KEY (`user_id`, `group_id`),
                                FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
                                FOREIGN KEY (`group_id`) REFERENCES `groups` (`id`) ON DELETE CASCADE
);

-- Chat Messages Table
CREATE TABLE `chat_messages` (
                                 `id` BIGINT NOT NULL AUTO_INCREMENT,
                                 `user_id` BIGINT NOT NULL,
                                 `chat_room_id` BIGINT NOT NULL,
                                 `message` VARCHAR(500) NOT NULL,
                                 `created_at` TIMESTAMP NOT NULL,
                                 `user_nickname` VARCHAR(50) NOT NULL,
                                 PRIMARY KEY (`id`),
                                 FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
                                 FOREIGN KEY (`chat_room_id`) REFERENCES `chat_rooms` (`id`) ON DELETE CASCADE
);

-- Review Posts Table
CREATE TABLE `review_posts` (
                                `id` BIGINT NOT NULL AUTO_INCREMENT,
                                `trip_id` BIGINT NOT NULL,
                                `user_id` BIGINT NOT NULL,
                                `title` VARCHAR(100) NOT NULL,
                                `content` VARCHAR(500) NOT NULL,
                                `created_at` TIMESTAMP NOT NULL,
                                `updated_at` TIMESTAMP NULL,
                                PRIMARY KEY (`id`),
                                FOREIGN KEY (`trip_id`) REFERENCES `trips` (`id`) ON DELETE CASCADE,
                                FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
);

-- Join Posts Table
CREATE TABLE `join_posts` (
                              `id` BIGINT NOT NULL AUTO_INCREMENT,
                              `trip_id` BIGINT NOT NULL,
                              `title` VARCHAR(100) NOT NULL,
                              `content` VARCHAR(500) NOT NULL,
                              `created_at` TIMESTAMP NOT NULL,
                              `updated_at` TIMESTAMP NULL,
                              PRIMARY KEY (`id`),
                              FOREIGN KEY (`trip_id`) REFERENCES `trips` (`id`) ON DELETE CASCADE
);

-- Review Posts Likes Table
CREATE TABLE `review_posts_likes` (
                                      `id` BIGINT NOT NULL AUTO_INCREMENT,
                                      `user_id` BIGINT NOT NULL,
                                      `review_post_id` BIGINT NOT NULL,
                                      PRIMARY KEY (`id`),
                                      FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
                                      FOREIGN KEY (`review_post_id`) REFERENCES `review_posts` (`id`) ON DELETE CASCADE
);

-- Review Posts Comments Table
CREATE TABLE `review_posts_comments` (
                                         `id` BIGINT NOT NULL AUTO_INCREMENT,
                                         `user_id` BIGINT NOT NULL,
                                         `review_post_id` BIGINT NOT NULL,
                                         `content` VARCHAR(500) NOT NULL,
                                         `created_at` TIMESTAMP NOT NULL,
                                         PRIMARY KEY (`id`),
                                         FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
                                         FOREIGN KEY (`review_post_id`) REFERENCES `review_posts` (`id`) ON DELETE CASCADE
);

-- Review Posts Replies Table
CREATE TABLE `review_posts_replies` (
                                        `id` BIGINT NOT NULL AUTO_INCREMENT,
                                        `user_id` BIGINT NOT NULL,
                                        `comment_id` BIGINT NOT NULL,
                                        `content` VARCHAR(500) NOT NULL,
                                        `created_at` TIMESTAMP NOT NULL,
                                        PRIMARY KEY (`id`),
                                        FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
                                        FOREIGN KEY (`comment_id`) REFERENCES `review_posts_comments` (`id`) ON DELETE CASCADE
);

-- Roles Table
CREATE TABLE `roles` (
                         `id` BIGINT NOT NULL AUTO_INCREMENT,
                         `name` VARCHAR(50) NULL,
                         PRIMARY KEY (`id`)
);

-- Users Roles Table
CREATE TABLE `users_roles` (
                               `user_id` BIGINT NOT NULL,
                               `role_id` BIGINT NOT NULL,
                               PRIMARY KEY (`user_id`, `role_id`),
                               FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
                               FOREIGN KEY (`role_id`) REFERENCES `roles` (`id`) ON DELETE CASCADE
);

-- Wishlists Table
CREATE TABLE `wishlists` (
                             `id` BIGINT NOT NULL AUTO_INCREMENT,
                             `review_id` BIGINT NOT NULL,
                             `user_id` BIGINT NOT NULL,
                             PRIMARY KEY (`id`),
                             FOREIGN KEY (`review_id`) REFERENCES `review_posts` (`id`) ON DELETE CASCADE,
                             FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
);

-- Place Review Table
CREATE TABLE `place_review` (
                                `id` BIGINT NOT NULL AUTO_INCREMENT,
                                `trip_location_id` BIGINT NOT NULL,
                                `review_post_id` BIGINT NOT NULL,
                                `content` VARCHAR(500) NOT NULL,
                                PRIMARY KEY (`id`),
                                FOREIGN KEY (`trip_location_id`) REFERENCES `trip_locations` (`id`) ON DELETE CASCADE,
                                FOREIGN KEY (`review_post_id`) REFERENCES `review_posts` (`id`) ON DELETE CASCADE
);

-- Join Post Likes Table
CREATE TABLE `join_post_likes` (
                                   `id` BIGINT NOT NULL AUTO_INCREMENT,
                                   `user_id` BIGINT NOT NULL,
                                   `join_post_id` BIGINT NOT NULL,
                                   PRIMARY KEY (`id`),
                                   FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
                                   FOREIGN KEY (`join_post_id`) REFERENCES `join_posts` (`id`) ON DELETE CASCADE
);

-- Join Post Comments Table
CREATE TABLE `join_post_comments` (
                                      `id` BIGINT NOT NULL AUTO_INCREMENT,
                                      `user_id` BIGINT NOT NULL,
                                      `join_post_id` BIGINT NOT NULL,
                                      `content` VARCHAR(500) NOT NULL,
                                      `created_at` TIMESTAMP NOT NULL,
                                      PRIMARY KEY (`id`),
                                      FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
                                      FOREIGN KEY (`join_post_id`) REFERENCES `join_posts` (`id`) ON DELETE CASCADE
);

-- Join Post Replies Table
CREATE TABLE `join_post_replies` (
                                     `id` BIGINT NOT NULL AUTO_INCREMENT,
                                     `user_id` BIGINT NOT NULL,
                                     `join_post_comment_id` BIGINT NOT NULL,
                                     `content` VARCHAR(500) NOT NULL,
                                     `created_at` TIMESTAMP NOT NULL,
                                     PRIMARY KEY (`id`),
                                     FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
                                     FOREIGN KEY (`join_post_comment_id`) REFERENCES `join_post_comments` (`id`) ON DELETE CASCADE
);

show tables;