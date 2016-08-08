package ua.gov.dp.econtact.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ua.gov.dp.econtact.Const;
import ua.gov.dp.econtact.R;
import ua.gov.dp.econtact.interfaces.ListListener;
import ua.gov.dp.econtact.model.GeoAddress;
import ua.gov.dp.econtact.model.Ticket;
import ua.gov.dp.econtact.model.TicketStates;
import ua.gov.dp.econtact.model.category.Category;
import ua.gov.dp.econtact.model.category.CategoryWithImages;
import ua.gov.dp.econtact.util.CategoryUtils;
import ua.gov.dp.econtact.util.DateUtil;

/**
 * Created by Yalantis
 * 7/25/15.
 *
 * @author Andrew Khristyan
 */
public class TicketsAdapter extends SelectableAdapter<TicketsAdapter.TicketsViewHolder> {

    private Context mContext;
    private List<Ticket> mTickets;
    private ListListener mLayoutClickListener;

    public TicketsAdapter(final Context context, final List<Ticket> tickets, final ListListener layoutClickListener) {
        mContext = context;
        mTickets = tickets;
        mLayoutClickListener = layoutClickListener;
    }

    public List<Ticket> getTickets() {
        return mTickets;
    }

    public void addTickets(List<Ticket> tickets) {
        mTickets.addAll(tickets);
        notifyItemRangeInserted(mTickets.size() - tickets.size(), tickets.size());
    }

    public void clear() {
        int itemCount = getItemCount();
        mTickets.clear();
        notifyItemRangeRemoved(0, itemCount);
    }

    @Override
    public TicketsViewHolder onCreateViewHolder(final ViewGroup parent, final int viewType) {
        return new TicketsViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_ticket, parent, false));
    }

    @Override
    public void onBindViewHolder(final TicketsViewHolder holder, final int position) {
        holder.bindData(mTickets.get(position));
        holder.relativeLayout.setTag(position);
        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mLayoutClickListener.onListItemClick((int) v.getTag());
            }
        });
        holder.relativeLayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return mLayoutClickListener.onListItemLongClick((int) view.getTag());
            }
        });
    }

    @Override
    public void onBindViewHolder(TicketsViewHolder holder, int position, List<Object> payloads) {
        super.onBindViewHolder(holder, position, payloads);
    }

    @Override
    public int getItemCount() {
        return mTickets.size();
    }

    public class TicketsViewHolder extends RecyclerView.ViewHolder {

        private TextView mTextViewTitle;
        private TextView mTextViewLikes;
        private TextView mTextViewAddress;
        private TextView mTextViewDueDate;
        private TextView mTextViewDaysLeft;
        private ImageView mImageViewCategory;
        private RelativeLayout relativeLayout;

        public TicketsViewHolder(final View itemView) {
            super(itemView);
            mTextViewAddress = (TextView) itemView.findViewById(R.id.txt_address);
            mTextViewLikes = (TextView) itemView.findViewById(R.id.txt_likes);
            mTextViewTitle = (TextView) itemView.findViewById(R.id.txt_title);
            mTextViewDaysLeft = (TextView) itemView.findViewById(R.id.txt_days_left);
            mTextViewDueDate = (TextView) itemView.findViewById(R.id.txt_due_date);
            mImageViewCategory = (ImageView) itemView.findViewById(R.id.img_category);
            relativeLayout = (RelativeLayout) itemView.findViewById(R.id.relative_layout);
        }

        private void bindData(final Ticket ticket) {

            Category ticketCategory = ticket.getCategory();
            if (ticketCategory == null) {
                mImageViewCategory.setImageResource(R.drawable.ic_doc);
            } else {
                long categoryId = ticketCategory.getId();
                final String categoryImageUrl = CategoryUtils.getCategoryImageById(categoryId);
                Picasso.with(mContext)
                        .load(categoryImageUrl)
                        .error(R.drawable.ic_doc)
                        .placeholder(R.drawable.ic_doc)
                        .into(mImageViewCategory);
            }

            mTextViewTitle.setText(TextUtils.isEmpty(ticket.getTitle())
                    ? mContext.getString(R.string.empty_category) : ticket.getTitle());

            TicketStates currentState = TicketStates.getTicketStateById(ticket.getState().getId());
            mTextViewLikes.setText(String.valueOf(ticket.getLikesCounter()));
            // // TODO: 23.06.2016 Uncomment when countdown working right
           /* if (currentState == TicketStates.IN_PROGRESS) {
                String daysString = getTimeLeft(ticket);
                mTextViewDaysLeft.setText(daysString);
                mTextViewDaysLeft.setVisibility(View.VISIBLE);
            } else {
                mTextViewDaysLeft.setVisibility(View.INVISIBLE);
            }*/

            mTextViewDaysLeft.setVisibility(View.INVISIBLE);
            mTextViewDueDate.setText(ticket.getCreated() == 0 ? "" : DateUtil.getDueDateFormattedDate(
                    (currentState == TicketStates.DONE ? ticket.getCompleted() : ticket.getCreated())
                            * Const.MILLIS_IN_SECOND));
            String addressText = mContext.getString(R.string.empty_address);
            if (ticket.getAddress() != null && ticket.getAddress().getStreet() != null && ticket.getAddress().getHouse() != null) {
                addressText = ticket.getAddress().getStreet().getName() + "," + ticket.getAddress().getHouse().getName();
            } else if (ticket.getGeoAddress() != null) {
                addressText = GeoAddress.generateAddressLabel(ticket.getGeoAddress());
            }
            mTextViewAddress.setText(addressText);
            boolean isSelected = isSelected(getAdapterPosition());
            relativeLayout.setBackgroundResource(isSelected ? R.drawable.list_item_shape_selected : R.drawable.list_item_shape);
        }

        private String getTimeLeft(final Ticket ticket) {
            long timeDifference = ticket.getDeadline() - (Calendar.getInstance().getTimeInMillis()) / Const.MILLIS_IN_SECOND;
            long days = TimeUnit.SECONDS.toDays(timeDifference > 0 ? timeDifference : timeDifference * -1);
            int daysCount = (int) (timeDifference > 0 ? days : days * -1);
            int twoWeeks = 14;
            int fourWeeks = 28;
            if (Math.abs(daysCount) < twoWeeks) {
                return mContext.getResources().getQuantityString(R.plurals.task_days, daysCount, daysCount);
            } else if (Math.abs(daysCount) < fourWeeks) {
                int weeksCount = daysCount / 7;
                return mContext.getResources().getQuantityString(R.plurals.task_weeks, weeksCount, weeksCount);
            } else {
                Calendar startCalendar = new GregorianCalendar();
                startCalendar.setTime(Calendar.getInstance().getTime());
                Calendar endCalendar = new GregorianCalendar();
                endCalendar.setTimeInMillis(ticket.getDeadline() * Const.MILLIS_IN_SECOND);

                int diffYear = endCalendar.get(Calendar.YEAR) - startCalendar.get(Calendar.YEAR);
                int diffMonth = diffYear * 12 + endCalendar.get(Calendar.MONTH) - startCalendar.get(Calendar.MONTH);
                return mContext.getResources().getQuantityString(R.plurals.task_months, diffMonth, diffMonth);
            }
        }
    }
}