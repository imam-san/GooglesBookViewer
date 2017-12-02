package com.dubiel.sample.googlesbookviewer;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.dubiel.sample.googlesbookviewer.search.SearchManager;
import com.dubiel.sample.googlesbookviewer.search.searchitem.BookListItem;

import com.dubiel.sample.googlesbookviewer.search.searchitem.BookListItems;
import com.google.common.cache.LoadingCache;
import com.squareup.picasso.Picasso;

import java.util.concurrent.ExecutionException;

public class BookItemListAdapter extends RecyclerView.Adapter<BookItemListAdapter.ViewHolder> {

    static final private String TAG = "BookItemListAdapter";

    private Context context;
//    private BookListItem[] bookListItems;
    private LoadingCache<Integer, BookListItems> bookListItems;
    private int smallThumbnailWidth, smallThumbnailHeight;

    public BookItemListAdapter(Context context, LoadingCache<Integer, BookListItems> bookListItems) {
        this.context = context;
        this.bookListItems = bookListItems;

        smallThumbnailWidth = context.getResources().getInteger(R.integer.small_thumbnail_width);
        smallThumbnailHeight = context.getResources().getInteger(R.integer.small_thumbnail_height);
    }

//    public BookItemListAdapter(Context context, BookListItem[] bookListItems) {
//        this.context = context;
//        this.bookListItems = bookListItems;
//    }

    @Override
    public BookItemListAdapter.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.book_item, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final BookItemListAdapter.ViewHolder viewHolder, int i) {
        System.out.println("onBindViewHolder: " + i);

        int key = (int)Math.floor(i / SearchManager.MAX_RESULTS);
        System.out.println("key: " + key);

        try {
            BookListItems currentBookListItems = bookListItems.get(key);

            int bookListItemIndex = i % SearchManager.MAX_RESULTS;
            System.out.println("bookListItemIndex: " + bookListItemIndex);

            BookListItem currentBookListItem = currentBookListItems.getItems()[bookListItemIndex];

//            System.out.println("small thumbnail width: " + smallThumbnailWidth);
//            System.out.println("small thumbnail height: " + smallThumbnailHeight);

            viewHolder.selfLink = currentBookListItem.getSelfLink();
            try {
                Picasso.with(context).load(currentBookListItem.getVolumeInfo().getImageLinks().getSmallThumbnail())
                        .resize(smallThumbnailWidth, smallThumbnailHeight)
                        .into(viewHolder.smallThumbnail);
            } catch(NullPointerException e) {
                Log.i(BookItemListAdapter.TAG, "small thumbnail image of " + currentBookListItem.getSelfLink() + " not present");
            }
            viewHolder.title.setText(currentBookListItem.getVolumeInfo().getTitle());

            viewHolder.view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Context context = v.getContext();
                    Intent intent = new Intent(context, BookDetailActivity.class);
                    intent.putExtra(BookDetailActivityFragment.ARG_SELF_LINK, viewHolder.selfLink);

                    context.startActivity(intent);
                }
            });
        } catch(ExecutionException e) {
            System.out.println(e.getMessage());
        }

//        viewHolder.selfLink = bookListItems[i].getSelfLink();
//        Picasso.with(context).load(bookListItems[i].getVolumeInfo().getImageLinks().getSmallThumbnail())
//                .resize(SMALL_THUMBNAIL_WIDTH, SMALL_THUMBNAIL_HEIGHT)
//                .into(viewHolder.smallThumbnail);
//        viewHolder.title.setText(bookListItems[i].getVolumeInfo().getTitle());
//
//        viewHolder.view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Context context = v.getContext();
//                Intent intent = new Intent(context, BookDetailActivity.class);
//                intent.putExtra(BookDetailActivityFragment.ARG_SELF_LINK, viewHolder.selfLink);
//
//                context.startActivity(intent);
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return (int)bookListItems.size() * SearchManager.MAX_RESULTS;
    }

//    public void setBookListItems(BookListItem[] bookListItems) {
//        this.bookListItems = bookListItems;
//    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView smallThumbnail;
        private TextView title;

        public final View view;
        public String selfLink;

        public ViewHolder(View view) {
            super(view);

            this.view = view;

            title = (TextView)view.findViewById(R.id.book_item_title);
            smallThumbnail = (ImageView) view.findViewById(R.id.book_item_small_thumbnail);
        }
    }

}
