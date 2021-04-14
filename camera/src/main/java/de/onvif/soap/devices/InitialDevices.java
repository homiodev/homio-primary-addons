package de.onvif.soap.devices;

import de.onvif.soap.OnvifDeviceState;
import de.onvif.soap.SOAP;
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

public class InitialDevices {

    private SOAP soap;
    private OnvifDeviceState onvifDeviceState;
    private GetProfilesResponse profilesResponse;
    private Map<String, Profile> profileToken2Profile = new HashMap<>();
    private GetDeviceInformationResponse deviceInformation;
    private List<Scope> scopes;

    public InitialDevices(OnvifDeviceState onvifDeviceState) {
        this.onvifDeviceState = onvifDeviceState;
        this.soap = onvifDeviceState.getSoap();
    }

    public java.util.Date getDate() {
        Calendar cal;

        GetSystemDateAndTimeResponse response = new GetSystemDateAndTimeResponse();

        try {
            response = (GetSystemDateAndTimeResponse) soap.createSOAPDeviceRequest(new GetSystemDateAndTime(), response);
        } catch (SOAPException | ConnectException e) {
            e.printStackTrace();
            return null;
        }

        Date date = response.getSystemDateAndTime().getUTCDateTime().getDate();
        Time time = response.getSystemDateAndTime().getUTCDateTime().getTime();
        cal = new GregorianCalendar(date.getYear(), date.getMonth() - 1, date.getDay(), time.getHour(), time.getMinute(), time.getSecond());

        return cal.getTime();
    }

    public GetDeviceInformationResponse getDeviceInformation() {
        if (this.deviceInformation == null) {
            GetDeviceInformation getHostname = new GetDeviceInformation();
            GetDeviceInformationResponse response = new GetDeviceInformationResponse();
            try {
                response = (GetDeviceInformationResponse) soap.createSOAPDeviceRequest(getHostname, response);
            } catch (SOAPException | ConnectException e) {
                e.printStackTrace();
                return null;
            }
            this.deviceInformation = response;
        }
        return this.deviceInformation;
    }

    public String getHostname() {
        GetHostname getHostname = new GetHostname();
        GetHostnameResponse response = new GetHostnameResponse();
        try {
            response = (GetHostnameResponse) soap.createSOAPDeviceRequest(getHostname, response);
        } catch (SOAPException | ConnectException e) {
            e.printStackTrace();
            return null;
        }

        return response.getHostnameInformation().getName();
    }

    public boolean setHostname(String hostname) {
        SetHostname setHostname = new SetHostname();
        setHostname.setName(hostname);
        SetHostnameResponse response = new SetHostnameResponse();
        try {
            response = (SetHostnameResponse) soap.createSOAPDeviceRequest(setHostname, response);
        } catch (SOAPException | ConnectException e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }

    public List<User> getUsers() {
        GetUsers getUsers = new GetUsers();
        GetUsersResponse response = new GetUsersResponse();
        try {
            response = (GetUsersResponse) soap.createSOAPDeviceRequest(getUsers, response);
        } catch (SOAPException | ConnectException e) {
            e.printStackTrace();
            return null;
        }

        if (response == null) {
            return null;
        }

        return response.getUser();
    }

    public Capabilities getCapabilities() throws ConnectException, SOAPException {
        GetCapabilities getCapabilities = new GetCapabilities();
        GetCapabilitiesResponse response = new GetCapabilitiesResponse();

        try {
            response = (GetCapabilitiesResponse) soap.createSOAPRequest(getCapabilities, response, onvifDeviceState.getDeviceUri());
        } catch (SOAPException e) {
            throw e;
        }

        if (response == null) {
            return null;
        }

        return response.getCapabilities();
    }

    public List<Profile> getProfiles() {
        GetProfiles request = new GetProfiles();
        if (this.profilesResponse == null) {
            profilesResponse = new GetProfilesResponse();

            try {
                profilesResponse = (GetProfilesResponse) soap.createSOAPMediaRequest(request, profilesResponse);
            } catch (SOAPException | ConnectException e) {
                e.printStackTrace();
                return null;
            }

            if (profilesResponse == null) {
                return null;
            }
        }

        return profilesResponse.getProfiles();
    }

    public Profile getProfile(String profileToken) {
        if (!profileToken2Profile.containsKey(profileToken)) {
            GetProfile request = new GetProfile();
            GetProfileResponse response = new GetProfileResponse();

            request.setProfileToken(profileToken);

            try {
                response = (GetProfileResponse) soap.createSOAPMediaRequest(request, response);
            } catch (SOAPException | ConnectException e) {
                e.printStackTrace();
                return null;
            }

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

        try {
            response = (CreateProfileResponse) soap.createSOAPMediaRequest(request, response);
        } catch (SOAPException | ConnectException e) {
            e.printStackTrace();
            return null;
        }

        if (response == null) {
            return null;
        }

        return response.getProfile();
    }

    public List<Service> getServices(boolean includeCapability) {
        GetServices request = new GetServices();
        GetServicesResponse response = new GetServicesResponse();

        request.setIncludeCapability(includeCapability);

        try {
            response = (GetServicesResponse) soap.createSOAPDeviceRequest(request, response);
        } catch (SOAPException | ConnectException e) {
            e.printStackTrace();
            return null;
        }

        if (response == null) {
            return null;
        }

        return response.getService();
    }

    public List<Scope> getScopes() {
        if (this.scopes == null) {
            GetScopes request = new GetScopes();
            GetScopesResponse response = new GetScopesResponse();

            try {
                response = (GetScopesResponse) soap.createSOAPMediaRequest(request, response);
            } catch (SOAPException | ConnectException e) {
                e.printStackTrace();
                return null;
            }

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
