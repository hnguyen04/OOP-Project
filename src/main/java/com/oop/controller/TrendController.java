package com.oop.controller;

import com.oop.service.APICaller;
import com.oop.model.Item;
import com.opencsv.exceptions.CsvValidationException;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import com.oop.manager.SwitchManager;
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
    private Button returnPage;
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
                System.out.println(trends);
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
                Text trendTitle = new Text("Trend: " + trend);
                trendTitle.getStyleClass().add("trend-title");
                contentBox.getChildren().add(trendTitle);

                Text reasonText = new Text("Reason: " + details.get(0));
                reasonText.getStyleClass().add("reason-text");
                contentBox.getChildren().add(reasonText);

                for (int i = 1; i < details.size(); i++) {
                    Text citationText = new Text("Citation " + i + ": " + details.get(i));
                    citationText.getStyleClass().add("citation-text");
                    contentBox.getChildren().add(citationText);
                }
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
        returnPage.setOnAction(event -> {
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

    public void setPageNumberReturn(int pageNumber) {
        this.pageBefore = pageNumber;
    }

    public void setSearchQueryReturn(String searchText) {
        this.searchText = searchText;
    }

    @Override
    public TextField getSearchField() {
        return null;
    }
}
