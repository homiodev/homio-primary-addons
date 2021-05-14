package de.onvif.soap.devices;

import de.onvif.soap.OnvifDeviceState;
import de.onvif.soap.SOAP;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.onvif.ver10.device.wsdl.*;
import org.onvif.ver10.media.wsdl.*;
import org.onvif.ver10.schema.Capabilities;
import org.onvif.ver10.schema.Date;
import org.onvif.ver10.schema.*;

import javax.xml.soap.SOAPException;
import java.net.ConnectException;
import java.util.*;
import java.util.stream.Collectors;

@RequiredArgsConstructor
public class InitialDevices {

    private final OnvifDeviceState onvifDeviceState;
    private final SOAP soap;

    private GetProfilesResponse profilesResponse;
    private Map<String, Profile> profileToken2Profile = new HashMap<>();
    private GetDeviceInformationResponse deviceInformation;
    private List<Scope> scopes;
    private List<Service> services;

    public void dispose() {
        profilesResponse = null;
        profileToken2Profile = null;
        deviceInformation = null;
        scopes = null;
    }

    public java.util.Date getDate() {
        Calendar cal;

        GetSystemDateAndTimeResponse response = new GetSystemDateAndTimeResponse();

        response = (GetSystemDateAndTimeResponse) soap.createSOAPDeviceRequest(new GetSystemDateAndTime(), response);

        Date date = response.getSystemDateAndTime().getUTCDateTime().getDate();
        Time time = response.getSystemDateAndTime().getUTCDateTime().getTime();
        cal = new GregorianCalendar(date.getYear(), date.getMonth() - 1, date.getDay(), time.getHour(), time.getMinute(), time.getSecond());

        return cal.getTime();
    }

    public GetDeviceInformationResponse getDeviceInformation() {
        if (this.deviceInformation == null) {
            GetDeviceInformation getHostname = new GetDeviceInformation();
            this.deviceInformation = new GetDeviceInformationResponse();
            this.deviceInformation = (GetDeviceInformationResponse) soap.createSOAPDeviceRequest(getHostname, this.deviceInformation);
        }
        return this.deviceInformation;
    }

    public String getHostname() {
        GetHostname getHostname = new GetHostname();
        GetHostnameResponse response = new GetHostnameResponse();
        response = (GetHostnameResponse) soap.createSOAPDeviceRequest(getHostname, response);
        return response == null ? null : response.getHostnameInformation().getName();
    }

    public void setHostname(String hostname) {
        SetHostname setHostname = new SetHostname();
        setHostname.setName(hostname);
        SetHostnameResponse response = new SetHostnameResponse();
        soap.createSOAPDeviceRequest(setHostname, response);
    }

    public List<User> getUsers() {
        GetUsers getUsers = new GetUsers();
        GetUsersResponse response = new GetUsersResponse();
        response = (GetUsersResponse) soap.createSOAPDeviceRequest(getUsers, response);
        return response == null ? null : response.getUser();
    }

    public Capabilities getCapabilities() throws ConnectException, SOAPException {
        GetCapabilities request = new GetCapabilities();
        GetCapabilitiesResponse response = new GetCapabilitiesResponse();

        response = (GetCapabilitiesResponse) soap.createSOAPRequest(request, response, onvifDeviceState.getServerDeviceUri(), onvifDeviceState.getServerDeviceIpLessUri());
        return response == null ? null : response.getCapabilities();
    }

    public List<Profile> getProfiles() {
        GetProfiles request = new GetProfiles();
        if (this.profilesResponse == null) {
            profilesResponse = new GetProfilesResponse();
            profilesResponse = (GetProfilesResponse) soap.createSOAPMediaRequest(request, profilesResponse);
        }

        return profilesResponse == null ? null : profilesResponse.getProfiles();
    }

    public Profile getProfile(String profileToken) {
        if (!profileToken2Profile.containsKey(profileToken)) {
            GetProfile request = new GetProfile();
            GetProfileResponse response = new GetProfileResponse();

            request.setProfileToken(profileToken);

            response = (GetProfileResponse) soap.createSOAPMediaRequest(request, response);

            if (response == null) {
                return null;
            }

            profileToken2Profile.put(profileToken, response.getProfile());
        }
        return profileToken2Profile.get(profileToken);
    }

    public Profile createProfile(String name) {
        CreateProfile request = new CreateProfile();
        CreateProfileResponse response = new CreateProfileResponse();

        request.setName(name);

        response = (CreateProfileResponse) soap.createSOAPMediaRequest(request, response);
        return response == null ? null : response.getProfile();
    }

    public List<Service> getServices() {
        if (services == null) {
            GetServices request = new GetServices().setIncludeCapability(true);
            GetServicesResponse response = (GetServicesResponse) soap.createSOAPDeviceRequest(request, new GetServicesResponse());
            services = response == null ? null : response.getService();
        }
        return services;
    }

    public List<Scope> getScopes() {
        if (this.scopes == null) {
            GetScopes request = new GetScopes();
            GetScopesResponse response = new GetScopesResponse();

            response = (GetScopesResponse) soap.createSOAPMediaRequest(request, response);
            if (response == null) {
                return null;
            }

            this.scopes = response.getScopes();
        }
        return this.scopes;
    }

    @SneakyThrows
    public void setName(String name) {
        this.scopes = null;
        if (!getName().equals(name)) {
            SetScopes request = new SetScopes();
            request.getScopes().add("odm:name:" + name);
            SetScopesResponse response = new SetScopesResponse();
            soap.createSOAPDeviceRequest(request, response);
            this.scopes = null;
        }
    }

    public String getName() {
        List<String> nameScopes = getScope("odm:name:");
        if (!nameScopes.isEmpty()) {
            return nameScopes.get(nameScopes.size() - 1).substring("odm:name:".length());
        }
        nameScopes = getScope("onvif://www.onvif.org/name/");
        return nameScopes.isEmpty() ? "" : nameScopes.get(nameScopes.size() - 1).substring("onvif://www.onvif.org/name/".length());
    }

    @SneakyThrows
    public String reboot() {
        SystemReboot request = new SystemReboot();
        SystemRebootResponse response = new SystemRebootResponse();
        response = (SystemRebootResponse) soap.createSOAPMediaRequest(request, response);
        return response == null ? null : response.getMessage();
    }

    public List<String> getScope(String name) {
        return getScopes().stream().map(Scope::getScopeItem).filter(s -> s.startsWith(name)).collect(Collectors.toList());
    }
}