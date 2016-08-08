package ua.gov.dp.econtact.fragment.ticket;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.Bind;
import ua.gov.dp.econtact.App;
import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.activity.ImageViewActivity;
import ua.gov.dp.econtact.activity.TicketActivity;
import ua.gov.dp.econtact.adapter.GalleryAdapter;
import ua.gov.dp.econtact.fragment.BaseFragment;
import ua.gov.dp.econtact.interfaces.ListListener;
import ua.gov.dp.econtact.interfaces.TicketDetailsListener;
import ua.gov.dp.econtact.model.Performer;
import ua.gov.dp.econtact.model.Ticket;
import ua.gov.dp.econtact.model.TicketStates;
import ua.gov.dp.econtact.util.DateUtil;

/**
 * Created by Yalantis
 * 03.09.2015.
 *
 * @author Kirill-Penzykov
 */
public class TicketsDetailsFragment extends BaseFragment implements ListListener {

    private static final String TICKET = "ticket";
    @Bind(R.id.txt_view_ticket_title)
    TextView mTxtViewTitle;
    @Bind(R.id.txt_view_badge)
    TextView mTxtViewBadge;
    @Bind(R.id.registered_at)
    TextView mTxtViewRegDate;
    @Bind(R.id.txt_view_completed)
    TextView mTxtViewSolveDate;
    @Bind(R.id.created_at)
    TextView mTxtViewCreateDate;
    @Bind(R.id.txt_view_manager)
    TextView mTxtViewManager;
    @Bind(R.id.txt_ticket_description)
    TextView mTxtViewDescription;
    @Bind(R.id.like_ticket_button)
    Button mLikeTicketButton;
    @Bind(R.id.see_on_map_button)
    Button mSeeOnMapButton;
    @Bind(R.id.see_answer_button)
    Button mSeeAnswerButton;
    @Bind(R.id.see_whole_text)
    Button mSeeWholeTextButton;
    @Bind(R.id.recycler_view_photos)
    RecyclerView mRecyclerViewGallery;
    @Bind(R.id.relative_solve_date)
    RelativeLayout mRelativeLayoutSolve;
    @Bind(R.id.relative_manager)
    RelativeLayout mRelativeLayoutManager;
    @Bind(R.id.relative_reg_date)
    RelativeLayout mRelativeLayoutReg;
    @Bind(R.id.txt_view_finished)
    TextView mTxtViewFinishedDate;
    @Bind(R.id.relative_finished_date)
    RelativeLayout mRelativeLayoutFinished;

    private TicketDetailsListener mTicketDetailsListener;
    private Ticket mTicket;
    private boolean isFromMap;
    private GalleryAdapter mGalleryAdapter;


    public static TicketsDetailsFragment newInstance() {
        return new TicketsDetailsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        App.dataManager.saveTicketToDB(mTicket);
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        try {
            mTicketDetailsListener = (TicketDetailsListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.getClass().getSimpleName()
                    + " should implement " + TicketDetailsListener.class.getSimpleName());
        }
        isFromMap = activity.getIntent().getBooleanExtra(TicketActivity.IS_FROM_MAP_KEY, false);
        mTicket = App.dataManager.getCurrentTicket();
        if(mTicket!=null) {
            mGalleryAdapter = new GalleryAdapter(mTicket.getFiles(), activity, this);
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ticket_details;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isAdded()) {
            mChangeTitleListener.changeTitle(mTicket.getTicketId());
        }
        mRecyclerViewGallery.setLayoutManager(new LinearLayoutManager(mActivity, LinearLayoutManager.HORIZONTAL, false));
        bindData();
        setListeners();

        mGalleryAdapter.notifyDataSetChanged();
    }

    private void setListeners() {
        mLikeTicketButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                likeTicketClick();
            }
        });
        mSeeOnMapButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                seeOnMapClick();
            }
        });
        mSeeAnswerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                seeAnswerClick();
            }
        });
        mSeeWholeTextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                seeWholeTextButton();
            }
        });
    }

    private void likeTicketClick() {
        mTicketDetailsListener.startFbLogin();
    }

    private void seeOnMapClick() {
        if (isFromMap) {
            mActivity.onBackPressed();
        } else {
            mTicketDetailsListener.showTicketOnMap();
        }
    }

    private void seeAnswerClick() {
        mTicketDetailsListener.openTicketAnswer();
    }

    private void seeWholeTextButton() {
        // TODO: implement method
    }

    private void bindData() {
        final int millisInSecond = 1000;
        if (mTicket != null) {
            fillImages();
            if ((mTicket.getAnswers() != null && !mTicket.getAnswers().isEmpty())
                    || mTicket.getState().getId() == Const.TICKET_STATUS_REJECTED) {
                mSeeAnswerButton.setVisibility(View.VISIBLE);
            }

            mTxtViewTitle.setText(mTicket.getTitle());
            mTxtViewCreateDate.setText(DateUtil.getFormattedDate(mTicket.getCreated() * millisInSecond));
            if (mTicket.getDeadline() > 0) {
                mTxtViewSolveDate.setText(DateUtil.getFormattedDate(mTicket.getDeadline() * millisInSecond));
            } else {
                mRelativeLayoutSolve.setVisibility(View.GONE);
            }
            if (mTicket.getStartDate() > 0) {
                mTxtViewRegDate.setText(DateUtil.getFormattedDate(mTicket.getStartDate() * millisInSecond));
            } else {
                mRelativeLayoutReg.setVisibility(View.GONE);
            }
            if (mTicket.getPerformers().isEmpty()) {
                mRelativeLayoutManager.setVisibility(View.GONE);
            } else {
                for (Performer performer : mTicket.getPerformers()) {
                    mTxtViewManager.append(getString(R.string.performer, performer.getOrganization(), performer.getPerson()));
                }
            }
            mTxtViewDescription.setText(mTicket.getBody());
            TicketStates ticketStates = TicketStates.getTicketStateById(mTicket.getState().getId());
            mTxtViewBadge.setBackgroundResource(ticketStates.getStateDrawable());
            mTxtViewBadge.setText(mTicket.getState().getName());
            if (isFromMap) {
                mSeeOnMapButton.setText(R.string.label_back_to_map);
            } else if (mTicket.getGeoAddress() == null
                    || TextUtils.isEmpty(mTicket.getGeoAddress().getLongitude())
                    || TextUtils.isEmpty(mTicket.getGeoAddress().getLatitude())) {
                mSeeOnMapButton.setVisibility(View.INVISIBLE);
            }


            mLikeTicketButton.setText(String.valueOf(mTicket.getLikesCounter()));
            if (ticketStates == TicketStates.DONE && mTicket.getCompleted() > 0) {
                mRelativeLayoutFinished.setVisibility(View.VISIBLE);
                mTxtViewFinishedDate.setText(DateUtil.getFormattedDate(mTicket.getStartDate() * millisInSecond));
            } else {
                mRelativeLayoutFinished.setVisibility(View.GONE);
            }
        }
    }

    private void fillImages() {
        if (!mTicket.getFiles().isEmpty()) {
            mRecyclerViewGallery.setAdapter(mGalleryAdapter);

        } else {
            mRecyclerViewGallery.setVisibility(View.GONE);
        }
    }

    @Override
    public void onListItemClick(final int position) {
        navigateToImageScreen(getActivity(), mTicket, position);
    }

    private void navigateToImageScreen(final Activity activity, final Ticket ticket, final int position) {
        Intent intent = new Intent(activity, ImageViewActivity.class);
        intent.putExtra(ImageViewActivity.EXTRA_ITEM, position);
        activity.startActivity(intent);
    }

    @Override
    public boolean onListItemLongClick(final int position) {
        return false;
    }

    public void onFbLiked(final int likesCount) {
        App.dataManager.updateTicket(mTicket, likesCount);
        mLikeTicketButton.setText(String.valueOf(mTicket.getLikesCounter()));
        getActivity().setResult(Activity.RESULT_OK);
    }
}
