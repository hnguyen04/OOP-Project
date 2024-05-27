package com.oop.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import com.oop.exception.InvalidSearchQueryException;
import com.oop.exception.ServerNoResponseException;
import com.oop.manager.SwitchManager;
import com.oop.service.APICaller;
import com.oop.exception.NetworkException;

import javafx.scene.control.Alert;
import org.json.simple.parser.ParseException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;

import javafx.scene.Cursor;

import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

public class MainController extends BaseController {

    private static final int IDLE_TIMEOUT = 500;
    private Timeline idleTimeline;
    private String lastSearchQuery;

    @FXML
    private TextField searchField;

    @FXML
    private VBox suggestions;

    public void addSuggestions(List<String> suggestionsResult) throws IOException {
        suggestions.getChildren().clear();
        for (String suggestion : suggestionsResult) {
            VBox suggestionField = new VBox();
            Label suggestionLabel = new Label(suggestion);
            suggestionField.getChildren().add(suggestionLabel);
            suggestionField.setStyle(
                    "-fx-padding:5px;-fx-font-size: 15px;-fx-border-color: rgb(15, 76,117);-fx-border-width: 0px 0px 1px 0px;-fx-border-radius: 0px 0px 10px 10px;");
            suggestionField.setOnMouseClicked(event -> {
                searchField.setText(suggestionLabel.getText());
                try {
                    APICaller.checkConnectNetWork();
                } catch (NetworkException e) {

                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Network connect error!");
                    alert.setHeaderText(null);
                    alert.setContentText("Please check your connect and retry!");
                    alert.showAndWait();
                }
                try {
                    SwitchManager.goSearchPage(this, event);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
            suggestionField.setOnMouseEntered((EventHandler<Event>) event -> suggestionField.setStyle(
                    "-fx-background-color: #dae7f3;-fx-background-radius: 10px;-fx-padding:5px;-fx-font-size: 15px;-fx-border-color: rgb(15, 76,117);-fx-border-width: 0px 1px 1px 0px;-fx-border-radius: 0px 0px 10px 10px;"));
            suggestionField.setOnMouseExited((EventHandler<Event>) event -> suggestionField.setStyle(
                    "-fx-padding:5px;-fx-font-size: 15px;-fx-border-color: rgb(15, 76,117);-fx-border-width: 0px 0px 1px 0px;-fx-border-radius: 0px 0px 10px 10px;"));
            suggestions.getChildren().add(suggestionField);
        }
        suggestions.setCursor(Cursor.HAND);
    }

    public void handleIdleEvent() throws NetworkException {
        String searchQuery = searchField.getText();
        if (searchQuery.equals(lastSearchQuery)) {
            idleTimeline.stop();
            return;
        }
        lastSearchQuery = searchQuery;
        List<String> suggestionsResults = new ArrayList<>();
        try {
            suggestionsResults = APICaller.querySuggest(searchQuery);
        } catch (URISyntaxException | IOException | ParseException e) {
            e.printStackTrace();
            return;
        } catch (ServerNoResponseException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Server is not responding");
            alert.setHeaderText(null);
            alert.setContentText("Please try connect to server!");
            alert.showAndWait();
        }
        try {
            addSuggestions(suggestionsResults);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initialize() throws IOException, ParseException {
        searchField.setOnKeyReleased(this::handleKeyReleased);
    }

    public void handleKeyReleased(KeyEvent event) {
        if (event.getCode().equals(KeyCode.ENTER)) {
            try {
                SwitchManager.goSearchPage(this, event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            if (idleTimeline == null) {
                idleTimeline = new Timeline(new KeyFrame(Duration.millis(IDLE_TIMEOUT), evt -> {
                    try {
                        handleIdleEvent();
                    } catch (NetworkException e) {
                        e.printStackTrace();
                    }
                }));
                idleTimeline.setCycleCount(Timeline.INDEFINITE);
            }
            idleTimeline.playFromStart();
        }
    }

    @Override
    public TextField getSearchField() {
        return this.searchField;
    }
}
