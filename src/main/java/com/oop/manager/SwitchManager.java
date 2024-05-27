package com.oop.manager;

import com.oop.controller.BaseController;
import com.oop.controller.DetailController;
import com.oop.controller.SearchController;
import com.oop.controller.TrendController;
import com.oop.exception.NetworkException;
import com.oop.exception.ServerNoResponseException;
import com.oop.model.Item;
import com.oop.service.APICaller;
import com.opencsv.exceptions.CsvValidationException;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import org.json.simple.parser.ParseException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;

public class SwitchManager {
    private static Stage stage;

    private static Scene scene;

    private static Parent root;

    public static void goDetailPage(BaseController baseController, Event event, Item item, int pageNumber,
                                    String searchField)
            throws IOException, CsvValidationException, java.text.ParseException, URISyntaxException, ParseException {
        FXMLLoader loader = new FXMLLoader(baseController.getClass().getResource("/view/Detail.fxml"));
        root = loader.load();
        DetailController detailController = loader.getController();
        detailController.setItem(item);
        try {
            detailController.initialize();
        } catch (ServerNoResponseException e) {
            throw new RuntimeException(e);
        }
        detailController.setPageNumberReturn(pageNumber);
        detailController.setSearchQueryReturn(searchField);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void goHomePage(BaseController baseController, Event event) throws IOException {
        root = FXMLLoader.load(Objects.requireNonNull(baseController.getClass().getResource("/view/Main.fxml")));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public static void goSearchPage(BaseController baseController, Event event) throws IOException {
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
            APICaller.checkServerResponse();
        } catch (ServerNoResponseException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Server no response!");
            alert.setHeaderText(null);
            alert.setContentText("Plese check server and retry!");
            alert.showAndWait();
        }
        String searchText = baseController.getSearchField().getText().trim();
        if (!searchText.isEmpty()) {
            FXMLLoader loader = new FXMLLoader(baseController.getClass().getResource("/view/SearchResults.fxml"));
            root = loader.load();
            SearchController searchControllerNew = loader.getController();
            searchControllerNew.setSearchText(searchText);
            searchControllerNew.initialize();
            stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Warning");
            alert.setHeaderText(null);
            alert.setContentText("Please enter something in the search box before clicking search!");
            alert.showAndWait();
        }
    }

    public static void returnSearchPage(BaseController baseController, ActionEvent event, int pageNumber,
                                        String searchText) throws IOException {
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
            APICaller.checkServerResponse();
        } catch (ServerNoResponseException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Server no response!");
            alert.setHeaderText(null);
            alert.setContentText("Plese check server and retry!");
            alert.showAndWait();
        }
        FXMLLoader loader = new FXMLLoader(baseController.getClass().getResource("/view/SearchResults.fxml"));
        Parent root = loader.load(); // Load content from SearchResults.fxml
        SearchController searchController = loader.getController();
        searchController.setSearchPage(pageNumber);
        searchController.setSearchText(searchText); // Truyền nội dung sang SearchController
        searchController.initialize();
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }
    public static void goTrendPage(BaseController baseController, Event event, Item item, int pageNumber,
                                   String searchField)
            throws IOException, CsvValidationException, java.text.ParseException, URISyntaxException, ParseException {
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
            APICaller.checkServerResponse();
        } catch (ServerNoResponseException e) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Server no response!");
            alert.setHeaderText(null);
            alert.setContentText("Plese check server and retry!");
            alert.showAndWait();
        }
        FXMLLoader loader = new FXMLLoader(baseController.getClass().getResource("/view/Trend.fxml"));
        root = loader.load();
        TrendController trendController = loader.getController();
        trendController.setItem(item);
        trendController.initialize();
        trendController.setPageNumberReturn(pageNumber);
        trendController.setSearchQueryReturn(searchField);
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

}