package ua.gov.dp.econtact.util;

import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.model.GeoAddress;
import ua.gov.dp.econtact.model.Ticket;
import ua.gov.dp.econtact.model.TicketFiles;
import ua.gov.dp.econtact.model.address.Address;

import java.util.List;

/**
 * Created by Yalantis
 * 08.09.2015.
 *
 * @author Aleksandr
 */
public final class IdGeneratorUtil {

    private static long MIN_ID = Long.MAX_VALUE - 1000;
    private static long MAX_ID = Long.MAX_VALUE;

    private IdGeneratorUtil() {

    }

    public static long getDraftTicketId() {
        List<Ticket> drafts = App.dataManager.getTicketsByState(new long[]{Const.TICKET_STATUS_DRAFT});
        boolean idExists;
        for (long i = MIN_ID; i < MAX_ID; i++) {
            idExists = false;
            for (int j = 0; j < drafts.size(); j++) {
                if (drafts.get(j).getId() == i) {
                    idExists = true;
                    break;
                }
            }
            if (!idExists) {
                return i;
            }
        }
        return MIN_ID;
    }

    public static long getDraftAddressId() {
        List<Address> addresses = App.dataManager.getAddressesGreaterThan(MIN_ID);
        boolean idExists;
        for (long i = MIN_ID; i < MAX_ID; i++) {
            idExists = false;
            for (int j = 0; j < addresses.size(); j++) {
                if (addresses.get(j).getId() == i) {
                    idExists = true;
                    break;
                }
            }
            if (!idExists) {
                return i;
            }
        }
        return MIN_ID;
    }

    public static long getDraftFileId() {
        List<TicketFiles> files = App.dataManager.getFilesGreaterThan(MIN_ID);
        boolean idExists;
        for (long i = MIN_ID; i < MAX_ID; i++) {
            idExists = false;
            for (int j = 0; j < files.size(); j++) {
                if (files.get(j).getId() == i) {
                    idExists = true;
                    break;
                }
            }
            if (!idExists) {
                return i;
            }
        }
        return MIN_ID;
    }

    public static long getGeoAddressId() {
        List<GeoAddress> geoAddresses = App.dataManager.getGeoAddresses();
        return geoAddresses.isEmpty() ? 0 : geoAddresses.get(geoAddresses.size() - 1).getId() + 1;
    }
}
