package ua.gov.dp.econtact.fragment.ticket;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.fragment.BaseFragment;
import ua.gov.dp.econtact.interfaces.TicketAnswerListener;
import ua.gov.dp.econtact.model.Ticket;
import ua.gov.dp.econtact.model.TicketAnswer;

/**
 * Created by Yalantis
 * 03.09.2015.
 *
 * @author Kirill-Penzykov
 */
public class TicketsAnswerFragment extends BaseFragment implements AdapterView.OnItemClickListener {

    @Bind(R.id.list_files)
    ListView mFilesListView;
    @Bind(R.id.text_view_title)
    TextView mTitleTextView;
    @Bind(R.id.text_view_comment)
    TextView mCommentTextView;
    @Bind(R.id.button_yes)
    Button mYesButton;
    @Bind(R.id.button_no)
    Button mNoButton;

    private TicketAnswerListener mAnswerListener;
    private Ticket mTicket;

    public static TicketsAnswerFragment newInstance(Ticket ticket) {
        TicketsAnswerFragment fragment = new TicketsAnswerFragment();
        fragment.mTicket = ticket;
        return fragment;
    }

    @Override
    public void onAttach(final Activity activity) {
        super.onAttach(activity);
        mChangeTitleListener.changeTitle(getString(R.string.label_answer));
        mAnswerListener = (TicketAnswerListener) activity;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_ticket_answer;
    }

    @Override
    public void onViewCreated(final View view, final Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setListeners();
        bindData();
    }

    private void setListeners() {
        mNoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: implement onClick
            }
        });
        mYesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: implement onClick
            }
        });
    }

    private void bindData() {
        if (mTicket != null) {
            mTitleTextView.setText(mTicket.getTitle());
            if (mTicket.getState().getId() == Const.TICKET_STATUS_REJECTED) {
                mCommentTextView.setVisibility(View.VISIBLE);
                mCommentTextView.setText(mTicket.getComment());
            } else {
                mFilesListView.setAdapter(new ArrayAdapter<>(getActivity(), R.layout.item_answer, getFilesListView()));
                mFilesListView.setOnItemClickListener(this);
            }
        }
    }

    private List<String> getFilesListView() {
        List<String> list = new ArrayList<>();
        for (TicketAnswer answer : mTicket.getAnswers()) {
            if (!TextUtils.isEmpty(answer.getOriginName())) {
                list.add(answer.getOriginName());
            } else {
                list.add(answer.getFilename());
            }
        }
        return list;
    }

    @Override
    public void onItemClick(final AdapterView<?> adapterView, final View view,
                            final int i, final long l) {
        mAnswerListener.onStartDownload(mTicket.getAnswers().get(i).getFilename());
    }
}
