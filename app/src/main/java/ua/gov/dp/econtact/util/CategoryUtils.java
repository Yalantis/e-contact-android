package ua.gov.dp.econtact.util;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.Nullable;
import android.util.Pair;

import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.adapter.CategoriesAdapter;
import ua.gov.dp.econtact.model.TicketStates;
import ua.gov.dp.econtact.model.category.CategoryWithImages;

/**
 * Created by Yalantis
 */
public final class CategoryUtils {

    private CategoryUtils() {

    }

    private static List<CategoryWithImages> getCategoryList(final Context context) {
        List<CategoryWithImages> categoryList = new ArrayList<>(App.dataManager.getAllCategories());
        final Collator uaCollator = Collator.getInstance(new Locale("uk", "UA"));
        Collections.sort(categoryList, new Comparator<CategoryWithImages>() {
            @Override
            public int compare(final CategoryWithImages lhs, final CategoryWithImages rhs) {
                return lhs.getName() == null
                        ? -1 : rhs.getName() == null
                        ? 1 : uaCollator.compare(lhs.getName(), rhs.getName());
            }
        });
        categoryList.add(0, new CategoryWithImages(-1, context.getString(R.string.category_all)));
        return categoryList;
    }

    private static Pair<List<Integer>, List<Integer>> getAllAndSelectedIndexes(
            final List<CategoryWithImages> categoryList) {
        List<Integer> selectedIndices = new ArrayList<>();
        List<Integer> allIndices = new ArrayList<>();
        Set<Long> catChoose = App.spManager.getCategoriesId();
        if (catChoose == null) {
            catChoose = new LinkedHashSet<>();
        }
        for (int i = 0; i < categoryList.size(); i++) {
            if (catChoose.contains(categoryList.get(i).getId())) {
                selectedIndices.add(i);
            }
            allIndices.add(i);
        }
        if (selectedIndices.isEmpty()) {
            selectedIndices.addAll(allIndices);
        }
        return new Pair<>(allIndices, selectedIndices);
    }

    public static List<CategoriesAdapter.CategoryAdapterItem> getCategories(final Context context) {
        List<CategoryWithImages> categoryList = getCategoryList(context);
        List<Integer> selectedIndexes = getAllAndSelectedIndexes(categoryList).second;

        List<CategoriesAdapter.CategoryAdapterItem> categories = new ArrayList<>();
        for (int i = 0; i < categoryList.size(); i++) {
            CategoryWithImages category = categoryList.get(i);
            categories.add(
                    new CategoriesAdapter.CategoryAdapterItem(selectedIndexes.contains(i), category));
        }

        return categories;
    }

    public static void setCategories(final Activity activity,
                                     final List<CategoriesAdapter.CategoryAdapterItem> adapterItems) {
        Set<Long> newIds = new LinkedHashSet<>();
        for (CategoriesAdapter.CategoryAdapterItem item : adapterItems) {
            if (item.isChecked()) {
                newIds.add(item.getCategory().getId());
            }
        }

        if (newIds.size() == 0) {
            Toaster.showShort(activity, activity.getString(R.string.category_none));
            return;
        }


        App.spManager.setCategoriesId(newIds);
        for (TicketStates ticketStates : TicketStates.values()) {
            if (ticketStates.isBackend()) {
                if (!AccountUtil.isLoggedIn(activity) && ticketStates == TicketStates.MY_TICKET) {
                    continue;
                }
                App.apiManager.getTicketsByStateAndCategory(ticketStates, Const.DEFAULT_OFFSET,
                        Const.DEFAULT_AMOUNT, newIds.toArray(new Long[newIds.size()]));
            }
        }
    }

    public static boolean isAllSelected(final Context context) {
        Pair<List<Integer>, List<Integer>> allAndSelected
                = getAllAndSelectedIndexes(getCategoryList(context));
        return allAndSelected.first.size() == allAndSelected.second.size();
    }

    @Nullable
    public static String getCategoryImageById(long id) {
        CategoryWithImages category = App.dataManager.getCategoryWithImage(id);
        if (category == null || category.getSmallImage() == null) {
            return null;
        } else {
            return category.getSmallImage();
        }
    }
}
