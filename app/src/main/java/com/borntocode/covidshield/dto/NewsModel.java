package com.borntocode.covidshield.dto;


import java.util.ArrayList;

public class NewsModel {

    ArrayList<articles> articles;

    public ArrayList<NewsModel.articles> getArticles() {
        return articles;
    }

    public void setArticles(ArrayList<NewsModel.articles> articles) {
        this.articles = articles;
    }

    public class articles{
        String author;
        String title;
        String description;
        String urlToImage;
        String publishedAt;
        String content;
        String url;

        public String getAuthor() {
            return author;
        }

        public void setAuthor(String author) {
            this.author = author;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getDescription() {
            return description;
        }

        public void setDescription(String description) {
            this.description = description;
        }

        public String getUrlToImage() {
            return urlToImage;
        }

        public void setUrlToImage(String urlToImage) {
            this.urlToImage = urlToImage;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public void setPublishedAt(String publishedAt) {
            this.publishedAt = publishedAt;
        }

        public String getContent() {
            return content;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }
    }
}

