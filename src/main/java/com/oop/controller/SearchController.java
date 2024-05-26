package com.oop.controller;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import java.util.ResourceBundle;

import com.oop.service.APICaller;
import com.oop.service.NetWorkException;
import com.oop.service.sorter.AuthorSorter;
import com.oop.service.sorter.DateSorter;
import com.oop.service.sorter.TitleSorter;
import com.oop.manager.SwitchManager;
import com.oop.model.Item;
import com.opencsv.exceptions.CsvValidationException;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.scene.text.TextFlow;
import javafx.scene.web.WebView;
import javafx.scene.Cursor;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import org.json.simple.parser.ParseException;

public class SearchController extends BaseController implements Initializable {

    @FXML
    private TextField searchField;
    @FXML
    private Button prevPageButton;
    @FXML
    private Button nextPageButton;
    @FXML
    private VBox suggestions;
    @FXML
    private Button homePage;
    @FXML
    private VBox searchResults;
    @FXML
    private ChoiceBox<String> categorySort;
    @FXML
    private Button searchButton;
    @FXML
    private Text currentPage;
    @FXML
    private Text categoryText;

    private static final int IDLE_TIMEOUT = 500;
    private Timeline idleTimeline;
    private String lastSearchQuery;

    private final int totalResultsPerPage = 10;
    private final ObservableList<String> criteriaList = FXCollections.observableArrayList(
            "Sort by title",
            "Sort by date",
            "Sort by author");

    private List<Item> searchResultList = new ArrayList<>();

    private int pageNumber = 1;

    @FXML
    private void handleSortCriteriaChange() {
        String selectedCriteria = categorySort.getValue();
        switch (selectedCriteria) {
            case "Sort by title": {
                TitleSorter sorter = new TitleSorter();
                searchResultList = sorter.sort(searchResultList);
                break;
            }
            case "Sort by date": {
                DateSorter sorter = new DateSorter();
                searchResultList = sorter.sort(searchResultList);
                break;
            }
            case "Sort by author": {
                AuthorSorter sorter = new AuthorSorter();
                searchResultList = sorter.sort(searchResultList);
                break;
            }
        }
        addSearchResult(searchResultList);
    }

    public void addSuggestions(List<String> suggestionsResult) throws IOException {
        suggestions.getChildren().clear();
        for (String suggestion : suggestionsResult) {
            VBox suggestionField = new VBox();
            Label suggestionLabel = new Label(suggestion);
            suggestionField.setStyle("-fx-background-color: rgb(15, 76, 117);-fx-text-fill: rgb(255, 255, 255);");
            suggestionField.getChildren().add(suggestionLabel);
            suggestionField.setOnMouseClicked(event -> {
                searchField.setText(suggestionLabel.getText());
                try {
                    SwitchManager.goSearchPage(this, event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            suggestionField.setOnMouseEntered(
                    event -> suggestionField.setStyle("-fx-border-color: #808080;-fx-background-color: #F0F8FF;"));
            suggestionField.setOnMouseExited(event -> suggestionField
                    .setStyle("-fx-background-color: rgb(15, 76, 117);-fx-text-fill: rgb(255, 255, 255);"));
            suggestions.getChildren().add(suggestionField);
        }
        suggestions.setCursor(Cursor.HAND);
    }

    private void addSearchResult(List<Item> itemList) {
        searchResults.getChildren().clear();
        int startIndex = totalResultsPerPage * (pageNumber - 1);
        int endIndex = Math.min(totalResultsPerPage * pageNumber, itemList.size());
        VBox scrollableContent = new VBox();
        for (int i = startIndex; i < endIndex; i++) {
            VBox itemNode = createItemNode(itemList.get(i));
            itemNode.getStyleClass().add("itemNode");
            scrollableContent.getChildren().add(itemNode);
        }
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(scrollableContent);
        scrollPane.setStyle("-fx-padding: 10px 0px 0px 10px;-fx-boder-width: 0px !important;");
        scrollPane.setPrefWidth(478);
        scrollPane.setPrefHeight(780);
        searchResults.getChildren().add(scrollPane);
    }

    private VBox createItemNode(Item item) {
        Hyperlink hyperlink = new Hyperlink(item.getArticleLink());
        hyperlink.setOnAction(event -> openWebView(item.getArticleLink()));
        //
        Text title = new Text(item.getArticleTitle());
        title.getStyleClass().add("title");
        //
        String dateTimeString = item.getCreationDate();
        String formattedDate;
        if (!dateTimeString.equals("None")) {
            DateTimeFormatter originalFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime dateTime = LocalDateTime.parse(dateTimeString, originalFormatter);
            DateTimeFormatter newFormatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
            formattedDate = dateTime.format(newFormatter);
        } else {
            formattedDate = "None";
        }
        Text date = new Text(formattedDate);
        date.getStyleClass().add("date");
        //
        TextFlow content = createTextFlow(
                item.getContent().substring(0, Math.min(item.getContent().length(), 250)) + " ...");
        content.setMinWidth(740);
        content.getStyleClass().add("content");
        //
        Button detailButton = new Button("Detail");
        detailButton.setStyle(
                "-fx-background-color: rgb(15, 76, 117); -fx-text-fill: rgb(187, 225, 250); -fx-font-weight: bold;");
        //
        detailButton.setOnAction(event -> {
            try {
                SwitchManager.goDetailPage(this, event, item, this.pageNumber, this.searchField.getText());
            } catch (IOException | CsvValidationException | java.text.ParseException | URISyntaxException
                    | ParseException e) {
                e.printStackTrace();
            }
        });
        Button trendButton = new Button("Trend");
        trendButton.setStyle(
                "-fx-background-color: rgb(15, 76, 117); -fx-text-fill: rgb(187, 225, 250); -fx-font-weight: bold;");
        //
        trendButton.setOnAction(event -> {
            try {
                SwitchManager.goTrendPage(this, event, item, this.pageNumber, this.searchField.getText());
            } catch (IOException | CsvValidationException | java.text.ParseException | URISyntaxException
                     | ParseException e) {
                e.printStackTrace();
            }
        });
        VBox itemNode = new VBox(title, hyperlink, date, content, detailButton,trendButton);
        itemNode.getStyleClass().add("itemNode");
        itemNode.setSpacing(5);
        itemNode.setPadding(new Insets(5));
        return itemNode;
    }

    private TextFlow createTextFlow(String content) {
        TextFlow textFlow = new TextFlow();
        content = content + ".....";
        Text text = new Text(content);
        textFlow.getChildren().add(text);
        textFlow.setMaxWidth(450);
        return textFlow;
    }

    public void getData() throws ParseException, IOException, URISyntaxException {
        searchResultList = APICaller.getSearchResult(searchField.getText());
        addSearchResult(searchResultList);
    }

    public void initialize(URL url, ResourceBundle resourceBundle) {
        searchField.setOnKeyReleased(event -> handleKeyReleased(event));
        try {
            getData();
            setupUIComponents();
        } catch (ParseException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void handleKeyReleased(KeyEvent event) {
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
                    } catch (NetWorkException e) {
                        e.printStackTrace();
                    }
                }));
                idleTimeline.setCycleCount(Timeline.INDEFINITE);
            }
            idleTimeline.playFromStart();
        }
    }

    private void handleIdleEvent() throws NetWorkException {
        String searchQuery = searchField.getText();
        if (searchQuery.equals(lastSearchQuery)) {
            idleTimeline.stop();
            return;
        }
        lastSearchQuery = searchQuery;
        System.out.println("Searching for: " + searchQuery);
        List<String> suggestionsResults = new ArrayList<>();
        try {
            suggestionsResults = APICaller.querySuggest(searchQuery);
        } catch (URISyntaxException | IOException | ParseException e) {
            e.printStackTrace();
            return;
        }
        try {
            addSuggestions(suggestionsResults);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupUIComponents() {
        categorySort.setItems(criteriaList);
        categoryText.setText("Category");
        currentPage.setText(pageNumber + "/" + Math.max(searchResultList.size() / totalResultsPerPage, 1));
        categorySort.setOnAction(event -> handleSortCriteriaChange());
        homePage.setOnAction(event -> {
            try {
                SwitchManager.goHomePage(this, event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        searchButton.setOnAction(event -> {
            try {
                SwitchManager.goSearchPage(this, event);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        nextPageButton.setOnAction(event -> {
            if (pageNumber < searchResultList.size() / totalResultsPerPage) {
                pageNumber++;
                addSearchResult(searchResultList);
                currentPage.setText(pageNumber + "/" + Math.max(searchResultList.size() / totalResultsPerPage, 1));

            }
        });
        prevPageButton.setOnAction(event -> {
            if (pageNumber > 1) {
                pageNumber--;
                addSearchResult(searchResultList);
                currentPage.setText(pageNumber + "/" + Math.max(searchResultList.size() / totalResultsPerPage, 1));

            }
        });
    }

    private void openWebView(String url) {
        Stage webStage = new Stage();
        WebView webView = new WebView();
        webView.getEngine().load(url);
        VBox root = new VBox(webView);
        Scene scene = new Scene(root, 800, 600);
        webStage.setScene(scene);
        webStage.show();
    }

    public void setSearchText(String searchText) {
        searchField.setText(searchText);
    }

    public void setSearchPage(int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public TextField getSearchField() {
        return searchField;
    }
}
