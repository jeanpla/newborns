package managedbeans;

import entities.core.Delivery;
import java.io.Serializable;
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
import sessionbeans.DeliveryFacadeLocal;

@Named("deliveryController")
@SessionScoped
public class DeliveryController implements Serializable {

    @EJB
    private DeliveryFacadeLocal ejbFacade;
    private List<Delivery> items = null;
    private List<Delivery> allItems = null;
    private Delivery selected;
    private int numberSons;
    
    @Inject
    private SessionUtil sessionUtil;

    @Inject
    private MotherController motherController;

    public DeliveryController() {
    }

    public Delivery getSelected() {
        return selected;
    }

    public void setSelected(Delivery selected) {
        this.selected = selected;
    }

    protected void setEmbeddableKeys() {
    }

    protected void initializeEmbeddableKey() {
    }

    private DeliveryFacadeLocal getFacade() {
        return ejbFacade;
    }

    public Delivery prepareCreate() {
        selected = new Delivery();        
        selected.setMother(motherController.getSelected());
        selected.setCreatedBy(sessionUtil.getCurrentUser());
        initializeEmbeddableKey();
        return selected;
    }

    public void create() {
        persist(PersistAction.CREATE, ResourceBundle.getBundle("/Bundle").getString("DeliveryCreated"));
        if (!JsfUtil.isValidationFailed()) {
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public void update() {
        persist(PersistAction.UPDATE, ResourceBundle.getBundle("/Bundle").getString("DeliveryUpdated"));
    }

    public void destroy() {
        persist(PersistAction.DELETE, ResourceBundle.getBundle("/Bundle").getString("DeliveryDeleted"));
        if (!JsfUtil.isValidationFailed()) {
            selected = null; // Remove selection
            items = null;    // Invalidate list of items to trigger re-query.
        }
    }

    public List<Delivery> getItems() {
        motherController.refreshSelected();
        items = motherController.getSelected().getDeliveries();
        refreshSelected();
        return items;
    }
    
    public List<Delivery> getAllItems() {
        allItems = getFacade().findAll();
        return allItems;
    }
    
    public Map getRegisteredItemsByType(String itemType) {
        getAllItems(); 
        Map registeredItems = new HashMap<>();
        
        for(Delivery item : allItems) {
            if (itemType.equals(item.getDeliveryType().getName())) {
                Calendar myCal = new GregorianCalendar();
                myCal.setTime(item.getDate());
                String key = String.valueOf(myCal.get(Calendar.YEAR));
                int value = 0;
                if (registeredItems.get(key) != null) {
                    value = (int) registeredItems.get(key);
                }
                registeredItems.put(key , value + 1);
            }
        }
        
        return registeredItems;
    }
    
    private void persist(PersistAction persistAction, String successMessage) {
        if (selected != null) {
            setEmbeddableKeys();
            try {
                if (persistAction == PersistAction.CREATE) {
                    getFacade().create(selected);
                } else if (persistAction != PersistAction.DELETE) {
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

    public Delivery getDelivery(java.lang.Long id) {
        return getFacade().find(id);
    }

    public void refreshSelected() {
        if (selected != null) {
            Long id = selected.getId();
            if (id != null) {
                selected = getDelivery(id);
            }
        }
    }

    public int getNumberSons() {
        return numberSons = motherController.getSelected().getSons().size();
    }

    public void setNumberSons(int numberSons) {
        this.numberSons = motherController.getSelected().getSons().size();
    }

    public List<Delivery> getItemsAvailableSelectMany() {
        return getFacade().findAll();
    }

    public List<Delivery> getItemsAvailableSelectOne() {
        return getFacade().findAll();
    }

    @FacesConverter(forClass = Delivery.class)
    public static class DeliveryControllerConverter implements Converter {

        @Override
        public Object getAsObject(FacesContext facesContext, UIComponent component, String value) {
            if (value == null || value.length() == 0) {
                return null;
            }
            DeliveryController controller = (DeliveryController) facesContext.getApplication().getELResolver().
                    getValue(facesContext.getELContext(), null, "deliveryController");
            return controller.getDelivery(getKey(value));
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
            if (object instanceof Delivery) {
                Delivery o = (Delivery) object;
                return getStringKey(o.getId());
            } else {
                Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, "object {0} is of type {1}; expected type: {2}", new Object[]{object, object.getClass().getName(), Delivery.class.getName()});
                return null;
            }
        }

    }

}
