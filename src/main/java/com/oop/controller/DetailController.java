package com.oop.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.Set;
import com.oop.manager.SwitchManager;
import com.oop.exception.NetworkException;
import com.oop.exception.ServerNoResponseException;
import com.oop.model.Item;
import com.oop.service.APICaller;
import com.opencsv.exceptions.CsvValidationException;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;

public class DetailController extends BaseController {

    private Item item;

    private HashMap<String, Set<String>> detailContent;

    @FXML
    private TextFlow contentBox;
    @FXML
    private Button returnButton;
    private String searchText;
    private int pageBefore;

    public void getDetailData() throws URISyntaxException, org.json.simple.parser.ParseException {
        if (item == null) {
            return;
        }
        try {
            detailContent = APICaller.getEntities(item.getContent());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }  catch (ServerNoResponseException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Server is not responding");
            alert.setHeaderText(null);
            alert.setContentText("Please try connect to server!");
            alert.showAndWait();
        }
        System.out.println(detailContent);
        createDetailContent();
    }

    public void createDetailContent() {
        boolean isRender;
        String[] words = item.getContent().split(" ");
        for (String word : words) {
            isRender = false;
            for (String key : detailContent.keySet()) {
                if (!isRender && detailContent.get(key).contains(word)) {
                    Text keyword = new Text(key + " ");
                    Text text = new Text(word + " ");
                    text.setStyle("-fx-fill: blue; -fx-underline: true;");
                    keyword.setStyle("-fx-font-weight: bold; -fx-fill: red;");
                    contentBox.getChildren().add(keyword);
                    contentBox.getChildren().add(text);
                    isRender = true;
                }
            }
            if (!isRender) {
                Text text = new Text(word + " ");
                contentBox.getChildren().add(text);
            }
        }
        contentBox.setStyle("-fx-padding:10px");
    }

    public void initialize() throws CsvValidationException, IOException, ParseException, URISyntaxException,
            org.json.simple.parser.ParseException, ServerNoResponseException {
            getDetailData();
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
