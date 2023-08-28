package com.alexeyefr.blackareacalculator;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

public class MainController {
    @FXML
    private Label resultLabel;

    @FXML
    private ImageView imageView;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Stage stage;

    @FXML
    public void onMenuExitClick(ActionEvent actionEvent) {
        stage.close();
    }

    @FXML
    public void onMenuLoadFileClick(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Выберите изображение");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Изображения", "*.jpg", "*.png", "*.jpeg")
        );

        File selectedFile = fileChooser.showOpenDialog(stage);
        if (selectedFile != null) {
            Image image = new Image(selectedFile.toURI().toString());
            imageView.setImage(image);
        }
    }

    @FXML
    public void onImageViewClick(MouseEvent mouseEvent) {
        double x = mouseEvent.getX();
        double y = mouseEvent.getY();
        Image image = imageView.getImage();
        if (image != null) {
            int imageWidth = (int) image.getWidth();
            int imageHeight = (int) image.getHeight();

            if (x >= 0 && x < imageWidth && y >= 0 && y < imageHeight) {
                // Получение цвета пикселя
                int pixelX = (int) x;
                int pixelY = (int) y;

                javafx.scene.image.PixelReader pixelReader = image.getPixelReader();
                javafx.scene.paint.Color color = pixelReader.getColor(pixelX, pixelY);

                double red = color.getRed();
                double green = color.getGreen();
                double blue = color.getBlue();

                // Обновление текста в resultLabel
                String colorInfo = String.format("Цвет пикселя: R=%.2f, G=%.2f, B=%.2f", red, green, blue);
                resultLabel.setText(colorInfo);
            }
        }
    }

    @FXML
    public void onMenuCalculateClick() {
        Image image = imageView.getImage();
        if (image != null) {
            int imageWidth = (int) image.getWidth();
            int imageHeight = (int) image.getHeight();

            Task<Void> calculateTask = new Task<>() {
                @Override
                protected Void call() {
                    for (int y = 0; y < imageHeight; y++) {
                        for (int x = 0; x < imageWidth; x++) {
                            javafx.scene.image.PixelReader pixelReader = image.getPixelReader();
                            javafx.scene.paint.Color color = pixelReader.getColor(x, y);

                            // Ваша логика обработки цвета пикселя здесь

                            // Обновление прогресса
                            double progress = (double) (y * imageWidth + x + 1) / (imageWidth * imageHeight);
                            updateProgress(progress, 1);

                            if (isCancelled()) {
                                return null;
                            }
                        }
                    }
                    return null;
                }
            };

            progressBar.progressProperty().bind(calculateTask.progressProperty());

            Thread thread = new Thread(calculateTask);
            thread.setDaemon(true);
            thread.start();

            calculateTask.setOnSucceeded(event -> {
                resultLabel.setText("Расчет завершен.");
                progressBar.progressProperty().unbind();
                progressBar.setProgress(1);
            });
        }
    }
}