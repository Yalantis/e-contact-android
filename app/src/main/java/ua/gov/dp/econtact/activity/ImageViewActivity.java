package ua.gov.dp.econtact.activity;

import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.adapter.ImagePagerAdapter;
import ua.gov.dp.econtact.model.TicketFiles;
import ua.gov.dp.econtact.view.ImageViewPager;

/**
 * @author Aleksandr
 *         Created on 11.09.2015.
 */
public class ImageViewActivity extends BaseActivity implements ViewPager.OnPageChangeListener {

    public static final String EXTRA_ITEM = "EXTRA_ITEM";
    public static final String EXTRA_PATHS = "EXTRA_PATHS";
    private List<Uri> images = new ArrayList<>();
    @Bind(R.id.view_pager)
    ImageViewPager mViewPager;

    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);
        showBackButton();
        if (getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_close);
        }

        /*
        if condition is true - we are expecting to come from TicketActivity,
        with current ticket in cache
        */
        if (getIntent().getStringArrayListExtra(EXTRA_PATHS) == null) {
            List<TicketFiles> files = App.dataManager.getCurrentTicket().getFiles();
            for (TicketFiles file : files) {
                images.add(Uri.parse(Const.URL_IMAGE + file.getFilename()));
            }
        } else {
            List<String> paths = getIntent().getStringArrayListExtra(EXTRA_PATHS);
            for (String path : paths) {
                images.add(Uri.fromFile(new File(path)));
            }
        }
        final int item = getIntent().getIntExtra(EXTRA_ITEM, 0);

        mViewPager.setAdapter(new ImagePagerAdapter(this, images, mViewPager));
        mViewPager.setCurrentItem(item);
        mViewPager.addOnPageChangeListener(this);

        setTitle(item, images.size());
    }

    private void setTitle(final int item, final int size) {
        setTitle(size > 1 ? (item + 1) + " / " + size : "");
    }

    @Override
    public void onPageScrolled(final int position, final float positionOffset, final int positionOffsetPixels) {
    }

    @Override
    public void onPageSelected(final int position) {
        setTitle(position, images.size());
    }

    @Override
    public void onPageScrollStateChanged(final int state) {
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }
}
