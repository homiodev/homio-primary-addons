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

        GetSystemDateAndTimeResponse response =
                soap.createSOAPDeviceRequestType(
                        new GetSystemDateAndTime(), GetSystemDateAndTimeResponse.class);

        Date date = response.getSystemDateAndTime().getUTCDateTime().getDate();
        Time time = response.getSystemDateAndTime().getUTCDateTime().getTime();
        cal =
                new GregorianCalendar(
                        date.getYear(),
                        date.getMonth() - 1,
                        date.getDay(),
                        time.getHour(),
                        time.getMinute(),
                        time.getSecond());

        return cal.getTime();
    }

    public GetDeviceInformationResponse getDeviceInformation() {
        if (deviceInformation == null) {
            GetDeviceInformation getHostname = new GetDeviceInformation();
            deviceInformation = soap.createSOAPDeviceRequestType(getHostname, GetDeviceInformationResponse.class);
        }
        return deviceInformation;
    }

    public String getHostname() {
        GetHostname getHostname = new GetHostname();
        GetHostnameResponse response =
                soap.createSOAPDeviceRequestType(getHostname, GetHostnameResponse.class);
        return response == null ? null : response.getHostnameInformation().getName();
    }

    public void setHostname(String hostname) {
        SetHostname setHostname = new SetHostname();
        setHostname.setName(hostname);
        soap.createSOAPDeviceRequest(setHostname, SetHostnameResponse.class);
    }

    public List<User> getUsers() {
        GetUsers getUsers = new GetUsers();
        GetUsersResponse response = soap.createSOAPDeviceRequestType(getUsers, GetUsersResponse.class);
        return response == null ? null : response.getUser();
    }

    @SneakyThrows
    public Capabilities getCapabilities() {
        GetCapabilities request = new GetCapabilities();
        GetCapabilitiesResponse response =
                soap.createSOAPRequest(
                        request,
                        GetCapabilitiesResponse.class,
                        onvifDeviceState.getServerDeviceUri(),
                        onvifDeviceState.getServerDeviceIpLessUri(),
                        false);
        return response == null ? null : response.getCapabilities();
    }

    public List<Profile> getProfiles() {
        if (this.profilesResponse == null) {
            profilesResponse = soap.createSOAPMediaRequest(new GetProfiles(), GetProfilesResponse.class);
        }

        return profilesResponse == null ? null : profilesResponse.getProfiles();
    }

    public Profile getProfile(String profileToken) {
        if (!profileToken2Profile.containsKey(profileToken)) {
            GetProfile request = new GetProfile();
            request.setProfileToken(profileToken);

            GetProfileResponse response = soap.createSOAPMediaRequest(request, GetProfileResponse.class);

            if (response == null) {
                return null;
            }

            profileToken2Profile.put(profileToken, response.getProfile());
        }
        return profileToken2Profile.get(profileToken);
    }

    public Profile createProfile(String name) {
        CreateProfile request = new CreateProfile();
        request.setName(name);

        CreateProfileResponse response =
                soap.createSOAPMediaRequest(request, CreateProfileResponse.class);
        return response == null ? null : response.getProfile();
    }

    public List<Service> getServices() {
        if (services == null) {
            GetServices request = new GetServices().setIncludeCapability(true);
            GetServicesResponse response =
                    soap.createSOAPDeviceRequestType(request, GetServicesResponse.class);
            services = response == null ? null : response.getService();
        }
        return services;
    }

    public List<Scope> getScopes() {
        if (this.scopes == null) {
            GetScopesResponse response =
                    soap.createSOAPMediaRequest(new GetScopes(), GetScopesResponse.class);
            if (response == null) {
                return null;
            }

            this.scopes = response.getScopes();
        }
        return this.scopes;
    }

    public String getName() {
        List<String> nameScopes = getScope("odm:name:");
        if (!nameScopes.isEmpty()) {
            return nameScopes.get(nameScopes.size() - 1).substring("odm:name:".length());
        }
        nameScopes = getScope("onvif://www.onvif.org/name/");
        return nameScopes.isEmpty()
                ? ""
                : nameScopes.get(nameScopes.size() - 1).substring("onvif://www.onvif.org/name/".length());
    }

    @SneakyThrows
    public void setName(String name) {
        this.scopes = null;
        if (!getName().equals(name)) {
            SetScopes request = new SetScopes();
            request.getScopes().add("odm:name:" + name);
            soap.createSOAPDeviceRequest(request, SetScopesResponse.class);
            this.scopes = null;
        }
    }

    @SneakyThrows
    public String reboot() {
        SystemReboot request = new SystemReboot();
        SystemRebootResponse response =
                soap.createSOAPMediaRequest(request, SystemRebootResponse.class);
        return response == null ? null : response.getMessage();
    }

    public List<String> getScope(String name) {
        return getScopes().stream()
                .map(Scope::getScopeItem)
                .filter(s -> s.startsWith(name))
                .collect(Collectors.toList());
    }
}
