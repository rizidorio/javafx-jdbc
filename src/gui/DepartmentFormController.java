package gui;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import db.DbException;
import gui.listerners.DataChangeListener;
import gui.util.Alerts;
import gui.util.Constraints;
import gui.util.Utils;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import model.entities.Department;
import model.exceptions.ValidationException;
import model.services.DepartmentService;

public class DepartmentFormController implements Initializable {
	
	private Department entity;
	
	private DepartmentService service;
	
	private List<DataChangeListener> dataChangeListeners = new ArrayList<>();
	
	@FXML
	private TextField txtId;
	
	@FXML
	private TextField txtName;
	
	@FXML
	private Label lblErroName;
	
	@FXML
	private Button btSalvar;
	
	@FXML
	private Button btCancelar;
	
	public void setDepartment(Department entity) {
		this.entity = entity;
	}
	
	public void setDepartmentService(DepartmentService service) {
		this.service = service;
	}
	
	public void subscribeDataChangeListener (DataChangeListener listener) {
		dataChangeListeners.add(listener);
	}
	
	@FXML
	public void onBtSalvarAction(ActionEvent event) {
		if (entity == null) {
			throw new IllegalStateException("A entidade está nula!");
		}
		if(service == null) {
			throw new IllegalStateException("O serviço está nulo");
		}
		
		try {
			entity = getFormData();
			service.saveOrUpdate(entity);
			notifyDataChangeListerners();
			Alerts.showAlert("Salvar", null, "Salvo com sucesso!", AlertType.INFORMATION);
			Utils.currentStage(event).close();
		}
		catch(ValidationException e) {
			setErrorsMessages(e.getErrors());
		}
		catch(DbException e) {
			Alerts.showAlert("Erro ao Salvar", null, e.getMessage(), AlertType.ERROR);
		}
		
	}
	
	private void notifyDataChangeListerners() {
		for (DataChangeListener listener : dataChangeListeners) {
			listener.onDataChanhed();
		}
	}

	private Department getFormData() {
		Department obj = new Department();
		
		ValidationException excepiton = new ValidationException("Erro de validação!");
		
		obj.setId(Utils.tryParseToInt(txtId.getText()));
		
		if(txtName.getText() == null || txtName.getText().trim().equals("")) {
			excepiton.addErros("Nome", "O campo não pode ser vazio!");
		}
		obj.setName(txtName.getText());
		
		if(excepiton.getErrors().size() > 0) {
			throw excepiton;
		}
		
		return obj;
	}

	@FXML
	public void onBtCancelarAction(ActionEvent event) {
		Utils.currentStage(event).close();
	}

	@Override
	public void initialize(URL url, ResourceBundle rb) {
		initializeNodes();
	}
	
	private void initializeNodes() {
		Constraints.setTextFieldInteger(txtId);
		Constraints.setTextFieldMaxLength(txtName, 30);
	}

	public void updateFormData() {
		if (entity == null) {
			throw new IllegalStateException("A Entidade está nula!");
		}
		
		txtId.setText(String.valueOf(entity.getId()));
		txtName.setText(entity.getName());
	}
	
	private void setErrorsMessages(Map<String, String> errors) {
		Set<String> fields = errors.keySet();
		
		if (fields.contains("Nome")) {
			lblErroName.setText(errors.get("Nome"));
		}
	}
}
