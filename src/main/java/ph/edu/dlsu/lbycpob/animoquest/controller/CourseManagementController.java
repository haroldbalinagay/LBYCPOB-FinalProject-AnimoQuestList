package ph.edu.dlsu.lbycpob.animoquest.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import net.rgielen.fxweaver.core.FxWeaver;
import net.rgielen.fxweaver.core.FxmlView;
import org.springframework.stereotype.Component;
import ph.edu.dlsu.lbycpob.animoquest.model.MasterlistCourse;
import ph.edu.dlsu.lbycpob.animoquest.service.CourseManagementService;

@Component
@FxmlView("course-management.fxml")
public class CourseManagementController {

    @FXML private TableView<MasterlistCourse> courseTable;
    @FXML private TableColumn<MasterlistCourse, Long> colId;
    @FXML private TableColumn<MasterlistCourse, String> colCode;
    @FXML private TableColumn<MasterlistCourse, String> colName;
    @FXML private TableColumn<MasterlistCourse, Integer> colUnits;
    @FXML private TableColumn<MasterlistCourse, Boolean> colPassFail;

    @FXML private TextField codeField;
    @FXML private TextField nameField;
    @FXML private TextField unitsField;
    @FXML private CheckBox passFailCheckBox;

    private final CourseManagementService courseService;
    private final FxWeaver fxWeaver;
    private MasterlistCourse selectedCourse;

    public CourseManagementController(CourseManagementService courseService, FxWeaver fxWeaver) {
        this.courseService = courseService;
        this.fxWeaver = fxWeaver;
    }

    @FXML
    public void initialize() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCode.setCellValueFactory(new PropertyValueFactory<>("code"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colUnits.setCellValueFactory(new PropertyValueFactory<>("units"));
        colPassFail.setCellValueFactory(new PropertyValueFactory<>("passFail"));

        courseTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                selectedCourse = newSel;
                codeField.setText(newSel.getCode());
                nameField.setText(newSel.getName());
                unitsField.setText(String.valueOf(newSel.getUnits()));
                passFailCheckBox.setSelected(newSel.isPassFail());
            }
        });

        loadCourses();
    }

    private void loadCourses() {
        ObservableList<MasterlistCourse> courses = FXCollections.observableArrayList(courseService.getAllCourses());
        courseTable.setItems(courses);
    }

    @FXML
    private void handleAddCourse() {
        try {
            int units = Integer.parseInt(unitsField.getText().trim());
            courseService.addCourse(
                    codeField.getText(),
                    nameField.getText(),
                    units,
                    passFailCheckBox.isSelected()
            );
            clearFields();
            loadCourses();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Units must be a valid integer (0 or greater).");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Save Error", e.getMessage());
        }
    }

    @FXML
    private void handleUpdateCourse() {
        if (selectedCourse == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a course to update.");
            return;
        }

        try {
            int units = Integer.parseInt(unitsField.getText().trim());
            courseService.updateCourse(
                    selectedCourse.getId(),
                    codeField.getText(),
                    nameField.getText(),
                    units,
                    passFailCheckBox.isSelected()
            );
            clearFields();
            loadCourses();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Input Error", "Units must be a valid integer (0 or greater).");
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Update Error", e.getMessage());
        }
    }

    @FXML
    private void handleDeleteCourse() {
        if (selectedCourse == null) {
            showAlert(Alert.AlertType.WARNING, "Selection Error", "Please select a course to delete.");
            return;
        }

        try {
            courseService.deleteCourse(selectedCourse.getId());
            clearFields();
            loadCourses();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Delete Error", e.getMessage());
        }
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            Parent root = fxWeaver.loadView(AdminDashboardController.class);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle("AnimoQuest - Admin Dashboard");
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void clearFields() {
        selectedCourse = null;
        codeField.clear();
        nameField.clear();
        unitsField.clear();
        passFailCheckBox.setSelected(false);
        courseTable.getSelectionModel().clearSelection();
    }

    private void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}