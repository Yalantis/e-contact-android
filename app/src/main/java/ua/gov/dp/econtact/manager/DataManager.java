package ua.gov.dp.econtact.manager;

import android.content.Context;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import de.greenrobot.event.EventBus;
import io.realm.Realm;
import io.realm.RealmConfiguration;
import io.realm.RealmResults;
import io.realm.Sort;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.api.ApiSettings;
import ua.gov.dp.econtact.event.address.CityEvent;
import ua.gov.dp.econtact.event.address.HouseEvent;
import ua.gov.dp.econtact.event.address.StreetEvent;
import ua.gov.dp.econtact.model.GeoAddress;
import ua.gov.dp.econtact.model.RealmMigration;
import ua.gov.dp.econtact.model.Ticket;
import ua.gov.dp.econtact.model.TicketFiles;
import ua.gov.dp.econtact.model.User;
import ua.gov.dp.econtact.model.address.Address;
import ua.gov.dp.econtact.model.address.City;
import ua.gov.dp.econtact.model.address.District;
import ua.gov.dp.econtact.model.address.House;
import ua.gov.dp.econtact.model.address.Street;
import ua.gov.dp.econtact.model.category.CategoryWithImages;
import ua.gov.dp.econtact.model.dto.CategoryListDTO;
import ua.gov.dp.econtact.model.stat.StatAll;

/**
 * Created by Yalantis
 * 25.09.2014.
 *
 * @author Andrew Hristyan
 */
public class DataManager implements Manager {

    private static final CacheManager sCacheManager = new CacheManager();
    private Realm mRealm;
    private Ticket mCurrentTicket;

    public Realm getRealm() {
        return mRealm;
    }

    public void setRealm(final Realm realm) {
        mRealm = realm;
    }

    @Override
    public void init(final Context context) {
        sCacheManager.init(context);

        RealmConfiguration configuration = new RealmConfiguration.Builder(context)
                .schemaVersion(Const.Realm.SCHEMA_VERSION)
                .migration(new RealmMigration())
                .build();


        mRealm = Realm.getInstance(configuration);
    }

    @Override
    public void clear() {
        sCacheManager.clear();
    }

    @Nullable
    public User getCurrentUser() {
        return mRealm.where(User.class).equalTo("id", App.spManager.getUserId()).findFirst();
    }

    public RealmResults<Ticket> getAllTickets() {
        return mRealm.where(Ticket.class).findAll();
    }

    public void saveProfileFromServerData(final User user) {
        sCacheManager.setUser(user);
        mRealm.beginTransaction();
        try {
            mRealm.copyToRealmOrUpdate(user);
            mRealm.commitTransaction();
        } catch (Exception e) {
            mRealm.cancelTransaction();
        }
    }

    public void deleteTicketByState(final long[] query) {
        mRealm.beginTransaction();
        try {
            for (long queryItem : query) {
                mRealm.where(Ticket.class).equalTo("state.id", queryItem).findAll().deleteAllFromRealm();
            }
            mRealm.commitTransaction();
        } catch (Exception e) {
            mRealm.cancelTransaction();
        }
    }

    public void deleteTicketByUserId(final long id) {
        mRealm.beginTransaction();
        try {
            mRealm.where(Ticket.class).equalTo("user.id", id)
                    .notEqualTo("state.id", Const.TICKET_STATUS_DRAFT).findAll().deleteAllFromRealm();
            mRealm.commitTransaction();
        } catch (Exception e) {
            mRealm.cancelTransaction();
        }
    }

    public void deleteTicketById(final long id) {
        List<Long> ids = new ArrayList<>();
        ids.add(id);
        deleteTicketById(ids);
    }

    public void deleteTicketById(final List<Long> ids) {
        mRealm.beginTransaction();
        try {
            for (Long id : ids) {
                mRealm.where(Ticket.class).equalTo(Ticket.ID, id).findAll().deleteAllFromRealm();
            }
            mRealm.commitTransaction();
        } catch (Exception e) {
            mRealm.cancelTransaction();
        }
    }

    public List<Ticket> getTicketsByState(final long[] query) {
        List<Ticket> tickets = new LinkedList<>();
        for (long queryItem : query) {
            RealmResults<Ticket> results = mRealm.where(Ticket.class)
                    .equalTo("state.id", queryItem)
                    .findAllSorted("created", Sort.DESCENDING);
            tickets.addAll(results);
        }
        return tickets;
    }

    public List<Ticket> getTicketsByUserId(final long userId) {
        List<Ticket> tickets = new LinkedList<>();
        RealmResults<Ticket> results = mRealm.where(Ticket.class)
                .equalTo("user.id", userId).notEqualTo("state.id", Const.TICKET_STATUS_DRAFT)
                .findAllSorted("created", Sort.DESCENDING);;
        tickets.addAll(results);
        return tickets;
    }

    public List<Ticket> getDraftTickets(final long[] query, final long userId) {
        List<Ticket> tickets = new LinkedList<>();
        for (long queryItem : query) {
            RealmResults<Ticket> results = mRealm.where(Ticket.class)
                    .equalTo("state.id", queryItem)
                    .equalTo("user.id", userId)
                    .findAllSorted("created", Sort.DESCENDING);
            tickets.addAll(results);
        }
        return tickets;
    }

    public List<Ticket> getTicketsByStateFilter(final long[] queryStates, final Long[] queryCategories) {
        List<Ticket> tickets = new ArrayList<>();
        for (long queryState : queryStates) {
            for (long queryCategory : queryCategories) {
                RealmResults<Ticket> results = mRealm.where(Ticket.class)
                        .equalTo("state.id", queryState)
                        .equalTo("category.id", queryCategory)
                        .findAll();
                tickets.addAll(results);
            }
        }
        return tickets;
    }

    public Ticket getTicketById(final long query) {
        return mRealm.copyFromRealm(mRealm.where(Ticket.class).equalTo(Ticket.ID, query).findFirst());
    }

    public void saveTicketsToDB(List<Ticket> tickets) {
        try {
            mRealm.beginTransaction();
            mRealm.copyToRealmOrUpdate(tickets);
            mRealm.commitTransaction();
        } catch (Exception e) {
            mRealm.cancelTransaction();
        }
    }

    public void saveTicketToDB(Ticket ticket) {
        try {
            mRealm.beginTransaction();
            mRealm.copyToRealmOrUpdate(ticket);
            mRealm.commitTransaction();
        } catch (Exception e) {
            mRealm.cancelTransaction();
        }
    }

    public void deleteUser() {
        long id = 0;
        id = App.spManager.getUserId();
        mRealm.beginTransaction();
        try {
            mRealm.where(User.class).equalTo(User.ID, id).findAll().clear();
            mRealm.commitTransaction();
        } catch (Exception e) {
            mRealm.cancelTransaction();
        }
    }

    public void purgeUsers() {
        mRealm.beginTransaction();
        mRealm.clear(User.class);
        mRealm.commitTransaction();
    }

    public void saveStatAllFromServerData(final StatAll statAll) {
        statAll.setId(StatAll.SINGLETON_ID);
        mRealm.beginTransaction();
        try {
            mRealm.copyToRealmOrUpdate(statAll);
            mRealm.commitTransaction();
        } catch (Exception e) {
            mRealm.cancelTransaction();
        }
    }

    public StatAll getStatAll() {
        return mRealm.where(StatAll.class).equalTo(StatAll.ID, StatAll.SINGLETON_ID).findFirst();
    }

    public void saveCategoriesFromServerData(final CategoryListDTO categoryListDTO) {
        for (CategoryWithImages category : categoryListDTO.getCategories()) {
            category.setSmallImage(ApiSettings.SCHEME + ApiSettings.HOSTNAME + category.getImages().getMedium());
        }
        mRealm.beginTransaction();
        try {
            mRealm.copyToRealmOrUpdate(categoryListDTO.getCategories());
            mRealm.commitTransaction();
        } catch (Exception e) {
            mRealm.cancelTransaction();
        }
    }

    public void saveDistrictsFromServerData(final List<District> districts) {
        mRealm.beginTransaction();
        try {
            mRealm.copyToRealmOrUpdate(districts);
            mRealm.commitTransaction();
        } catch (Exception e) {
            mRealm.cancelTransaction();
        }
    }

    public RealmResults<CategoryWithImages> getAllCategories() {
        return mRealm.where(CategoryWithImages.class).findAll();
    }

    public CategoryWithImages getCategoryWithImage(long id) {
        return mRealm.where(CategoryWithImages.class).equalTo(CategoryWithImages.ID, id).findFirst();
    }

    public RealmResults<District> getAllDistricts() {
        return mRealm.where(District.class).findAll();
    }

    public City getCityById(final long id) {
        return mRealm.where(City.class).equalTo(City.ID, id).findFirst();
    }

    public House getHouseById(final long id) {
        return mRealm.where(House.class).equalTo(House.ID, id).findFirst();
    }

    public Street getStreetById(final long id) {
        return mRealm.where(Street.class).equalTo(Street.ID, id).findFirst();
    }

    public District getDistrictById(final long id) {
        return mRealm.where(District.class).equalTo(District.ID, id).findFirst();
    }

    public RealmResults<City> getCitiesByDistrictId(final long id) {
        return mRealm.where(City.class).equalTo(City.DISTRICT_ID, id).findAll();
    }

    public RealmResults<Street> getStreetsByCityId(final long id) {
        return mRealm.where(Street.class).equalTo(Street.CITY_ID, id).findAll();
    }

    public RealmResults<House> getHousesByStreetId(final long id) {
        return mRealm.where(House.class).equalTo(House.STREET_ID, id).findAll();
    }

    public RealmResults<Address> getAddressesGreaterThan(final long id) {
        return mRealm.where(Address.class).greaterThanOrEqualTo(Address.ID, id).findAll();
    }

    public RealmResults<TicketFiles> getFilesGreaterThan(final long id) {
        return mRealm.where(TicketFiles.class).greaterThanOrEqualTo(TicketFiles.ID, id).findAll();
    }

    public void saveCitiesFromServerData(final List<City> data) {
        mRealm.beginTransaction();
        try {
            mRealm.copyToRealmOrUpdate(data);
            mRealm.commitTransaction();
        } catch (Exception e) {
            mRealm.cancelTransaction();
        }
        EventBus.getDefault().postSticky(new CityEvent());

    }

    public void saveStreetFromServerData(final List<Street> data) {
        mRealm.beginTransaction();
        try {
            mRealm.copyToRealmOrUpdate(data);
            mRealm.commitTransaction();
        } catch (Exception e) {
            mRealm.cancelTransaction();
        }
        EventBus.getDefault().postSticky(new StreetEvent());

    }

    public void saveHousesFromServerData(final List<House> data) {
        mRealm.beginTransaction();
        try {
            mRealm.copyToRealmOrUpdate(data);
            mRealm.commitTransaction();
        } catch (Exception e) {
            mRealm.cancelTransaction();
        }
        EventBus.getDefault().postSticky(new HouseEvent());

    }

    public void updateTicket(final Ticket ticket, final int likesCount) {
        mRealm.beginTransaction();
        try {
            ticket.setLikesCounter(likesCount);
            mRealm.commitTransaction();
        } catch (Exception e) {
            mRealm.cancelTransaction();
        }
    }

    public void setUserId(final long userId) {
        App.spManager.setUserId(userId);
    }

    public List<GeoAddress> getGeoAddresses() {
        return mRealm.where(GeoAddress.class).findAllSorted(GeoAddress.ID);
    }

    public void setRegistrInfo(final String email, final String firstName, final String middleName,
                               final String lastName, final String password, final Address mAddress) {
        sCacheManager.setSignUpInf(email, firstName, middleName, lastName, password, mAddress);
    }

    public String getEmail() {
        return sCacheManager.getEmail();
    }

    public String getFirstName() {
        return sCacheManager.getFirstName();
    }

    public String getMiddleName() {
        return sCacheManager.getMiddleName();
    }

    public String getLastName() {
        return sCacheManager.getLastName();
    }

    public String getPassword() {
        return sCacheManager.getPassword();
    }

    public Address getAddress() {
        return sCacheManager.getAddress();
    }

    public void setIsStartPhoneValidate(boolean isStartPhoneValidate) {
        sCacheManager.setStartPhoneValidate(isStartPhoneValidate);
    }

    public boolean getIsStartPhoneValidate() {
        return sCacheManager.isStartPhoneValidate();
    }

    public Ticket getCurrentTicket() {
        return mCurrentTicket;
    }

    public void setCurrentTicket(Ticket currentTicket) {
        mCurrentTicket = currentTicket;
    }
}
