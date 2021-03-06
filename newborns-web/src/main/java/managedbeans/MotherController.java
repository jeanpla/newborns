package managedbeans;

import entities.core.Mother;
import entities.core.Profile;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
import managedbeans.util.SessionUtil;
import sessionbeans.MotherFacadeLocal;

@Named("motherController")
@SessionScoped
public class MotherController implements Serializable {

    @EJB
    private MotherFacadeLocal ejbFacade;
    private List<Mother> items = null;
    private List<Mother> allItems = null;
    private Mother selected;

    @Inject
    private ProfileController profileController;
    
    @Inject
    private SessionUtil sessionUtil;
    
    private List<Mother> filteredItems;

    public MotherController() {
    }

    public Mother getSelected() {
        return selected;
    }

    public void setSelected(Mother selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private MotherFacadeLocal getFacade() {
        return ejbFacade;
    }

    public Mother prepareCreate() {
        selected = new Mother();
        selected.setProfile(new Profile());
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("MotherCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("MotherUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("MotherDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Mother> getItems() {
        if (items == null) {
            items = getFacade().findAll();
        }
        return items;
    }
         
    public List<Mother> getAllItems() {           
        allItems = getFacade().findAll();
        if (allItems == null) {
            allItems = new ArrayList<Mother>();
        }
        return allItems;
    }
  
    public Map getRegisteredItems() {
        
        getAllItems();
        Map registeredItems = new HashMap<>();
        
        for(Mother item : allItems) {            
             Calendar myCal = new GregorianCalendar();
                myCal.setTime(item.getProfile().getCreatedAt());
                String key = String.valueOf(myCal.get(Calendar.YEAR));
                int value = 0;
                if (registeredItems.get(key) != null) {
                    value = (int) registeredItems.get(key);
                }
                registeredItems.put(key , value + 1);         
        }
        return registeredItems;
    }
    
    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if(persistAction == PersistAction.CREATE) {
                    getFacade().createWithProfile(selected);
                }
                else if (persistAction != PersistAction.DELETE) {
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

    public Mother getMother(java.lang.Long id) {
        return getFacade().find(id);
    }
    
    public void refreshSelected() {
        if (selected != null) {
            Long id = selected.getId();
            if(id!=null){
                selected = getMother(selected.getId());
            }
        }
    }

    public List<Mother> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Mother> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Mother.class)
    public static class MotherControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            MotherController controller = (MotherController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "motherController");
            return controller.getMother(getKey(value));
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
            if (object instanceof Mother) {
                Mother o = (Mother) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Mother.class.getName()});
                return null;
            }
        }

    }

    public List<Mother> getFilteredItems() {
        return filteredItems;
    }

    public void setFilteredItems(List<Mother> filteredItems) {
        this.filteredItems = filteredItems;
    }
    
    public void profileView() {
        profileController.setSelected(selected.getProfile());
        profileController.refreshSelected();
        JsfUtil.redirect("/faces/roles/" + sessionUtil.getCurrentUser().getAccountType().getName() + 
                "/index-profile.xhtml");
    }
    
}
