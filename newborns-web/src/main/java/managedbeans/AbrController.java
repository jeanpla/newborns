package managedbeans;

import entities.ExamPhase;
import entities.tau.Abr;
import entities.tau.Exam;
import java.io.Serializable;
import java.util.Date;
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
import sessionbeans.AbrFacadeLocal;

@Named("abrController")
@SessionScoped
public class AbrController implements Serializable {

    @EJB
    private AbrFacadeLocal ejbFacade;
    private List<Abr> items = null;
    private Abr selected;
    
    @Inject 
    private SonController sonController;
    
    @Inject
    private OaeAabrController oaeAabrController;
    
    @Inject 
    private ExamPhaseController examPhaseController;
    
    @Inject
    private ExamTypeController examTypeController;
    
    @Inject
    private ExamController examController;

    public AbrController() {
    }

    public Abr getSelected() {
        return selected;
    }

    public void setSelected(Abr selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private AbrFacadeLocal getFacade() {
        return ejbFacade;
    }

    public Abr prepareCreate() {
        selected = new Abr();
        selected.setExam(new Exam());
        selected.getExam().setCreated(new Date());
        selected.getExam().setSon(sonController.getSelected());
        selected.getExam().setPhase(examPhaseController.getExamPhase(Long.parseLong(Integer.toString(oaeAabrController.getPhase()))));
        selected.getExam().setExamType(examTypeController.getExamType(new Long(3)));
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("AbrCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("AbrUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("AbrDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Abr> getItems() {
        sonController.refreshSelected();
        refreshSelected();
        items = getFacade().findBySon(sonController.getSelected());
        return items;
    }

    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction == PersistAction.CREATE) {
                    getFacade().create(selected);
                } else if (persistAction != PersistAction.DELETE) {
                    getFacade().edit(selected);
                    examController.setSelected(selected.getExam());
                    examController.update();
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
    
    private void updateSon() {
        System.out.println("Update son");
        sonController.update();
        sonController.refreshSelected();
        refreshSelected();
    }

    public Abr getAbr(java.lang.Long id) {
        return getFacade().find(id);
    }
    
    public void refreshSelected() {
        if(selected != null) {
            if(selected.getId() != null) {
                selected = getAbr(selected.getId());
            }
        }
    }

    public List<Abr> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Abr> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Abr.class)
    public static class AbrControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            AbrController controller = (AbrController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "abrController");
            return controller.getAbr(getKey(value));
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
            if (object instanceof Abr) {
                Abr o = (Abr) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Abr.class.getName()});
                return null;
            }
        }

    }

}
