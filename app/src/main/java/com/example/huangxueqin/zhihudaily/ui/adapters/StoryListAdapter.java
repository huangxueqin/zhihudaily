package com.example.huangxueqin.zhihudaily.ui.adapters;

import android.content.ContentValues;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.huangxueqin.zhihudaily.R;
import com.example.huangxueqin.zhihudaily.common.ArrayUtils;
import com.example.huangxueqin.zhihudaily.common.DateUtils;
import com.example.huangxueqin.zhihudaily.interfaces.IStoryListItemClickListener;
import com.example.huangxueqin.zhihudaily.models.story.SimpleStory;
import com.example.huangxueqin.zhihudaily.models.story.StoryResponse;
import com.example.huangxueqin.zhihudaily.provider.StoryContact;
import com.example.huangxueqin.zhihudaily.ui.widget.CirclePageIndicator;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by huangxueqin on 16-7-25.
 */
public class StoryListAdapter extends RecyclerView.Adapter<StoryListAdapter.ViewHolder> {
    public static final int TYPE_TOP_STORY = 0;
    public static final int TYPE_LIST_STORY = 1;
    public static final int TYPE_LIST_DATE = 2;

    private final Context context;
    private final SimpleStory[] topStories;
    private final List<StorySet> storiesList;

    private final List<Integer> groupOffsets;
    private Set<Long> readRecords;
    private IStoryListItemClickListener listener;

    private final int[] tmpIndexPath = {0, 0};

    public StoryListAdapter(Context context, StoryResponse latest, IStoryListItemClickListener listener) {
        this.context = context;
        this.topStories = latest.topStories;
        this.storiesList = ArrayUtils.asList(StorySet.asHeaderSet(context, latest.stories));
        this.groupOffsets = ArrayUtils.asList(0);
        this.readRecords = Collections.unmodifiableSet(new HashSet<Long>());
        this.listener = listener;
    }

    public void setReadRecords(Set<Long> records) {
        readRecords = Collections.unmodifiableSet(records);
        notifyDataSetChanged();
    }

    public void appendHistoryStories(StoryResponse stories) {
        groupOffsets.add(getTotalStoryCount() + groupOffsets.size());
        storiesList.add(StorySet.asStorySet(context, stories.stories, stories.date));
        notifyDataSetChanged();
    }

    public String headerOfPosition(int position) {
        // if top stories
        if (isTopStories(position)) {
            return storiesList.get(0).header;
        }
        getIndexPath(position, tmpIndexPath);
        return storiesList.get(tmpIndexPath[0]).header;
    }

    private boolean isTopStories(int position) {
        return position == 0 && !ArrayUtils.isEmpty(topStories);
    }

    /**
     * indexPath {m, n | n >= 0} for {@link }.get(m).stories.get(n)
     * indexPath {m, -1} for header
     * only lower bound checking
     * @param position
     * @param indexPath
     */
    private void getIndexPath(int position, int[] indexPath) {
        if (isTopStories(position)) {
            indexPath[0] = -1;
            return;
        }

        if (!ArrayUtils.isEmpty(topStories)) {
            position -= 1;
        }

        List<Integer> offsets = groupOffsets;
        int lastGroupSize = ArrayUtils.lastOf(storiesList).stories.size() + 1;
        if (position < offsets.get(0) || position >= ArrayUtils.lastOf(offsets) + lastGroupSize) {
            throw new ArrayIndexOutOfBoundsException("invalid position value");
        }

        if (position >= ArrayUtils.lastOf(offsets)) {
            indexPath[0] = offsets.size()-1;
            indexPath[1] = position - ArrayUtils.lastOf(offsets) - 1;
            return;
        }

        int low = 0, high = offsets.size()-1;
        while (high - low > 1) {
            int mid = (high + low) / 2;
            if (position < offsets.get(mid)) {
                high = mid;
            } else {
                low = mid;
            }
        }
        indexPath[0] = low;
        indexPath[1] = position - offsets.get(low) - 1;
    }

    private int getTotalStoryCount() {
        if (storiesList.size() == 0) { return 0; }
        return ArrayUtils.lastOf(groupOffsets) + 1 - groupOffsets.size()    // prior N-1 groups story count
                + ArrayUtils.lastOf(storiesList).stories.size();            // last group story count
    }

    @Override
    public int getItemViewType(int position) {
        getIndexPath(position, tmpIndexPath);
        if (tmpIndexPath[0] < 0) {
            return TYPE_TOP_STORY;
        } else if (tmpIndexPath[1] < 0) {
            return TYPE_LIST_DATE;
        } else {
            return TYPE_LIST_STORY;
        }
    }

    public Object getItem(int position) {
        getIndexPath(position, tmpIndexPath);
        if (tmpIndexPath[0] < 0) {
            return topStories;
        } else if (tmpIndexPath[1] < 0) {
            return storiesList.get(tmpIndexPath[0]).header;
        } else {
            return storiesList.get(tmpIndexPath[0]).stories.get(tmpIndexPath[1]);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View root = null;
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        if(viewType == TYPE_TOP_STORY) {
            root = inflater.inflate(R.layout.view_top_stories, parent, false);
        }
        else if(viewType == TYPE_LIST_STORY){
            root = inflater.inflate(R.layout.view_latest_news_list_item, parent, false);
        }
        else if(viewType == TYPE_LIST_DATE) {
            root = inflater.inflate(R.layout.view_latest_news_header, parent, false);
        }
        return new ViewHolder(root, viewType);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.position = position;
        int type = holder.viewType;
        if (type == TYPE_TOP_STORY && holder.topStoriesGallery.getAdapter() == null) {
            holder.topStoriesGallery.setAdapter(new TopStoryAdapter(listener, topStories));
            holder.pageIndicator.setViewPager(holder.topStoriesGallery);
        } else if (type == TYPE_LIST_STORY) {
            SimpleStory story = (SimpleStory) getItem(position);
            holder.title.setText(story.title);
            if (readRecords.contains(story.id)) {
                holder.title.setTextColor(context.getResources().getColor(R.color.item_read));
            } else {
                holder.title.setTextColor(context.getResources().getColor(R.color.item_unread));
            }
            Glide.with(holder.thumb.getContext()).load(story.images[0]).into(holder.thumb);
        } else if (type == TYPE_LIST_DATE) {
            holder.headerText.setText((String) getItem(position));
        }
    }

    @Override
    public int getItemCount() {
        return groupOffsets.size() + getTotalStoryCount() + (ArrayUtils.isEmpty(topStories) ? 0 : 1);
    }

    private static class StorySet {
        final List<SimpleStory> stories;
        final String header;

        StorySet(SimpleStory[] stories, String header) {
            this.stories = Arrays.asList(stories);
            this.header = header;
        }

        static StorySet asHeaderSet(Context context, SimpleStory[] stories) {
            return new StorySet(stories, context.getResources().getString(R.string.today_stories_header));
        }

        static StorySet asStorySet(Context context, SimpleStory[] stories, String date) {
            return new StorySet(stories, DateUtils.getReadableDateStr(context, date));
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        // for top gallery
        ViewPager topStoriesGallery;
        CirclePageIndicator pageIndicator;

        // for news list_item_news
        TextView title;
        ImageView thumb;

        // for news list_item_date
        TextView headerText;

        int viewType;
        int position;
        View root;

        public ViewHolder(View root, int viewType) {
            super(root);
            this.viewType = viewType;
            this.root = root;
            if(viewType == TYPE_LIST_STORY) {
                title = (TextView) root.findViewById(R.id.news_title);
                thumb = (ImageView) root.findViewById(R.id.news_thumb);
                root.setOnClickListener(this);
            }
            else if(viewType == TYPE_TOP_STORY){
                topStoriesGallery = (ViewPager) root.findViewById(R.id.top_stories_gallery);
                pageIndicator = (CirclePageIndicator) root.findViewById(R.id.top_stories_indicator);
            }
            else if(viewType == TYPE_LIST_DATE) {
                headerText = (TextView) root.findViewById(R.id.item_header);
            }
        }

        @Override
        public void onClick(View view) {
            SimpleStory story = (SimpleStory) getItem(position);
            if(listener != null) {
                listener.onRequestNews(story.id);
            }
            if(!readRecords.contains(story.id)) {
                ContentValues values = new ContentValues();
                values.put(StoryContact.ReadNewses.NEWS_ID, story.id);
                values.put(StoryContact.ReadNewses.NEWS_TITLE, story.title);
                values.put(StoryContact.ReadNewses.NEWS_TYPE, story.type);
                context.getContentResolver().insert(StoryContact.ReadNewses.CONTENT_URI, values);
                title.setTextColor(context.getResources().getColor(R.color.item_read));
            }
        }
    }
}
