/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Temporal;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

/**
 *
 * @author sylar
 */
@Entity
public class Person implements Serializable {
    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    
    @NotNull
    private Long run;
    
    @NotNull
    @Size(min = 1, max = 1, message = "Deve ingresar un caracter verificador")
    private String dvRun;
    
    @NotNull
    @Pattern(regexp = "\\D*", message = "El campo Nombres no debe contener números")
    private String name;
    
    @NotNull
    @Pattern(regexp = "\\D*", message = "El campo Primer apellido no debe contener números ni tildes")
    private String firstLastname;
    
    @NotNull
    @Pattern(regexp = "\\D*", message = "El campo Segundo apellido no puede contener números ni tildes")
    private String secondLastname;

    @NotNull(message= "Debe colocar una fecha de nacimiento")
    @Temporal(javax.persistence.TemporalType.DATE)
    @Past(message = "Coloque una fecha de nacimiento anterior al día de hoy")
    private Date birthDate;
    
    @JoinColumn(nullable = false)
    @ManyToOne
    private Gender gender;
    
    @JoinColumn(nullable = false)
    @ManyToOne
    private Forecast forecastHealth;  
    
    @NotNull
    private Boolean prais;
    
    @JoinColumn(nullable = false)
    @ManyToOne
    private Region region;
    
    @NotNull
    private String city;
    
    @NotNull
    private String municipality;
    
    @NotNull
    private Boolean ruralResidency;
    
    @ManyToOne
    private ResidencyType recidencyType;
    
    private String streetName;
    
    private String streetNumber;
    
    private String streetNameOther;
    
    private String fixPhoneNumber;
    
    private String movilePhoneNumber;
    
    @Pattern(regexp = "^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$" , message = "Ingrese un correo valido, e.g.=mail@mail.com")
    private String email;
    
    @NotNull( message = "Debe ingresar una sospecha de diagnóstico")
    private String diagnosticSuspicion;
    
    private String diagnosticConfirmation;
    
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getRun() {
        return run;
    }

    public void setRun(Long run) {
        this.run = run;
    }

    public String getDvRun() {
        return dvRun;
    }

    public void setDvRun(String dvRun) {
        this.dvRun = dvRun;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFirstLastname() {
        return firstLastname;
    }

    public void setFirstLastname(String firstLastname) {
        this.firstLastname = firstLastname;
    }

    public String getSecondLastname() {
        return secondLastname;
    }

    public void setSecondLastname(String secondLastname) {
        this.secondLastname = secondLastname;
    }

    public Date getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Date birthDate) {
        this.birthDate = birthDate;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public Forecast getForecastHealth() {
        return forecastHealth;
    }

    public void setForecastHealth(Forecast forecastHealth) {
        this.forecastHealth = forecastHealth;
    }

    public Boolean getPrais() {
        return prais;
    }

    public void setPrais(Boolean prais) {
        this.prais = prais;
    }

    public Region getRegion() {
        return region;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getMunicipality() {
        return municipality;
    }

    public void setMunicipality(String municipality) {
        this.municipality = municipality;
    }

    public Boolean getRuralResidency() {
        return ruralResidency;
    }

    public void setRuralResidency(Boolean ruralResidency) {
        this.ruralResidency = ruralResidency;
    }

    public ResidencyType getRecidencyType() {
        return recidencyType;
    }

    public void setRecidencyType(ResidencyType recidencyType) {
        this.recidencyType = recidencyType;
    }

    public String getStreetName() {
        return streetName;
    }

    public void setStreetName(String streetName) {
        this.streetName = streetName;
    }

    public String getStreetNumber() {
        return streetNumber;
    }

    public void setStreetNumber(String streetNumber) {
        this.streetNumber = streetNumber;
    }

    public String getStreetNameOther() {
        return streetNameOther;
    }

    public void setStreetNameOther(String streetNameOther) {
        this.streetNameOther = streetNameOther;
    }

    public String getFixPhoneNumber() {
        return fixPhoneNumber;
    }

    public void setFixPhoneNumber(String fixPhoneNumber) {
        this.fixPhoneNumber = fixPhoneNumber;
    }

    public String getMovilePhoneNumber() {
        return movilePhoneNumber;
    }

    public void setMovilePhoneNumber(String movilePhoneNumber) {
        this.movilePhoneNumber = movilePhoneNumber;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDiagnosticSuspicion() {
        return diagnosticSuspicion;
    }

    public void setDiagnosticSuspicion(String diagnosticSuspicion) {
        this.diagnosticSuspicion = diagnosticSuspicion;
    }

    public String getDiagnosticConfirmation() {
        return diagnosticConfirmation;
    }

    public void setDiagnosticConfirmation(String diagnosticConfirmation) {
        this.diagnosticConfirmation = diagnosticConfirmation;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Person)) {
            return false;
        }
        Person other = (Person) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return id.toString();
    }
    
}