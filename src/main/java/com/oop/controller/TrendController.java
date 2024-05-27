package com.oop.controller;

import com.oop.exception.NetworkException;
import com.oop.exception.ServerNoResponseException;
import com.oop.manager.SwitchManager;
import com.oop.service.APICaller;
import com.oop.model.Item;
import com.opencsv.exceptions.CsvValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Vector;

public class TrendController extends BaseController {
    private Item item;
    @FXML
    private VBox trendList;
    @FXML
    private Button returnButton;
    private int pageBefore;
    private String searchText;
    private HashMap<String, Vector<String>> trends;

    public void getTrendData() {
        if (item == null) {
            return;
        }
        try {
            trends = APICaller.trendDectect(item.getContent());
            if (!trends.isEmpty()) {
                createTrendContent();
            } else {
                // Hiển thị thông báo lỗi cho người dùng nếu không có dữ liệu trả về
                Text errorText = new Text("No trends data available.");
                trendList.getChildren().add(errorText);
            }
        } catch (IOException | URISyntaxException | org.json.simple.parser.ParseException e) {
            e.printStackTrace();
            // Hiển thị thông báo lỗi cho người dùng
            Text errorText = new Text("Failed to load trends: " + e.getMessage());
            trendList.getChildren().add(errorText);
        } catch (ServerNoResponseException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Server is not responding");
            alert.setHeaderText(null);
            alert.setContentText("Please try connect to server!");
            alert.showAndWait();
        }
    }

    public void createTrendContent() {
        trendList.getChildren().clear();

        // Tạo VBox để chứa nội dung xu hướng
        VBox contentBox = new VBox();
        contentBox.setSpacing(10); // Khoảng cách giữa các phần tử

        for (String trend : trends.keySet()) {
            Vector<String> details = trends.get(trend);
            if (details != null && !details.isEmpty()) {
                // Create a VBox for each trend to ensure spacing between elements
                VBox trendContent = new VBox();
                trendContent.setSpacing(5); // Khoảng cách giữa các phần tử trong một xu hướng

                Text trendTitleText = new Text("Trend: " + trend);
                trendTitleText.getStyleClass().add("trend-title");
                TextFlow trendTitle = new TextFlow(trendTitleText);
                trendContent.getChildren().add(trendTitle);

                Text reasonTextContent = new Text("Reason: " + details.get(0));
                reasonTextContent.getStyleClass().add("reason-text");
                TextFlow reasonText = new TextFlow(reasonTextContent);
                trendContent.getChildren().add(reasonText);

                for (int i = 1; i < details.size(); i++) {
                    Text citationTextContent = new Text("Citation " + i + ": " + details.get(i));
                    citationTextContent.getStyleClass().add("citation-text");
                    TextFlow citationText = new TextFlow(citationTextContent);
                    trendContent.getChildren().add(citationText);
                }

                // Add the trend content to the contentBox
                contentBox.getChildren().add(trendContent);
            }
        }

        // Tạo ScrollPane và đặt contentBox làm nội dung
        ScrollPane scrollPane = new ScrollPane(contentBox);
        scrollPane.setFitToWidth(true); // Đặt để khớp chiều rộng của ScrollPane với nội dung

        // Đặt các tùy chọn phong cách và kích thước
        scrollPane.setStyle("-fx-padding: 10px; -fx-border-width: 0px !important;");
        scrollPane.setPrefWidth(478);
        scrollPane.setPrefHeight(780);

        // Thêm ScrollPane vào VBox chính
        trendList.getChildren().add(scrollPane);
    }

    public void initialize() throws CsvValidationException, IOException, ParseException, URISyntaxException,
            org.json.simple.parser.ParseException {
        getTrendData();
        returnButton.setOnAction(event -> {
            try {
                SwitchManager.returnSearchPage(this, event, this.pageBefore, this.searchText);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public void setPageNumberReturn(int pageBefore) {
        this.pageBefore = pageBefore;
    }

    public void setSearchQueryReturn(String searchText) {
        this.searchText = searchText;
    }

    @Override
    public TextField getSearchField() {
        return null;
    }
}
