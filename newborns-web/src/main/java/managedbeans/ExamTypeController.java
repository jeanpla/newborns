package managedbeans;

import entities.ExamType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.enterprise.context.SessionScoped;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;
import javax.faces.convert.FacesConverter;
import javax.inject.Inject;
import javax.inject.Named;
import managedbeans.util.JsfUtil;
import managedbeans.util.JsfUtil.PersistAction;
import sessionbeans.ExamTypeFacadeLocal;

@Named("examTypeController")
@SessionScoped
public class ExamTypeController implements Serializable {

    @EJB
    private ExamTypeFacadeLocal ejbFacade;
    private List<ExamType> items = null;
    private ExamType selected;
    
    @Inject
    private OaeAabrController oaeAabrController;
    
    @Inject
    private SonController sonController;

    public ExamTypeController() {
    }

    public ExamType getSelected() {
        return selected;
    }

    public void setSelected(ExamType selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private ExamTypeFacadeLocal getFacade() {
        return ejbFacade;
    }

    public ExamType prepareCreate() {
        selected = new ExamType();
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("ExamTypeCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("ExamTypeUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("ExamTypeDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<ExamType> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                } else {
                    getFacade().remove(selected);
                }
                JsfUtil.addSuccessMessage(successMessage);
            } catch (EJBException ex) {
                String msg = "";
                Throwable cause = ex.getCause();
                if (cause != null) {
                    msg = cause.getLocalizedMessage();
                }
                if (msg.length() > 0) {
                    JsfUtil.addErrorMessage(msg);
                } else {
                    JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
                }
            } catch (Exception ex) {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, null, ex);
                JsfUtil.addErrorMessage(ex, ResourceBundle.getBundle("/Bundle").getString("PersistenceErrorOccured"));
            }
        }
    }

    public ExamType getExamType(java.lang.Long id) {
        return getFacade().find(id);
    }

    public List<ExamType> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<ExamType> getItemsAvailableSelectOne() {
        List<ExamType> allThem = getFacade().findAll();
        List<ExamType> allowed = new ArrayList<ExamType>();
        
        for (ExamType item : allThem) {
            if(oaeAabrController.getPhase() != 3) {
                if(item.getId() != 3)
                    allowed.add(item);
            } else {
                if(item.getId() == 3) {
                    allowed.add(item);
                }
            }
        }
        return allowed;
    }

    @FacesConverter(forClass = ExamType.class)
    public static class ExamTypeControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            ExamTypeController controller = (ExamTypeController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "examTypeController");
            return controller.getExamType(getKey(value));
        }

        java.lang.Long getKey(String value) {
            java.lang.Long key;
            key = Long.valueOf(value);
            return key;
        }

        String getStringKey(java.lang.Long value) {
            StringBuilder sb = new StringBuilder();
            sb.append(value);
            return sb.toString();
        }

        @Override
        public String getAsString(FacesContext facesContext, UIComponent component, Object object) {
            if (object == null) {
                return null;
            }
            if (object instanceof ExamType) {
                ExamType o = (ExamType) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), ExamType.class.getName()});
                return null;
            }
        }

    }

}
