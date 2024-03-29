package ru.netology.nmedia.adapter

import android.net.Uri
import android.text.Layout
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.core.net.toUri
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.CardAdBinding
import ru.netology.nmedia.databinding.CardPostBinding
import ru.netology.nmedia.dto.Ad
import ru.netology.nmedia.dto.FeedItem
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.functions.formatNumber
import ru.netology.nmedia.image.load
import ru.netology.nmedia.image.loading
import ru.netology.nmedia.url.UrlProvider

interface OnInteractionListener {
    fun onLike(post: Post) {}
    fun onEdit(post: Post) {}
    fun onRemove(post: Post) {}
    fun onRepost(post: Post) {}
    fun onVideo(post: Post) {}
    fun onPost(post: Post) {}
    fun onPhoto(post: Post) {}
}


class PostsAdapter(private val onInteractionListener: OnInteractionListener) :
    PagingDataAdapter<FeedItem, RecyclerView.ViewHolder>(PostDiffCallback) {
    override fun getItemViewType(position: Int): Int =
        when (getItem(position)) {
            is Ad -> R.layout.card_ad
            is Post -> R.layout.card_post
            null -> error("unknown item type ")
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        when (viewType) {
            R.layout.card_ad -> {
                val binding =
                    CardAdBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return AdViewHolder(binding)

            }

            R.layout.card_post -> {
                val binding =
                    CardPostBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                return PostViewHolder(binding, onInteractionListener)
            }

            else -> error("unknown view type: $viewType ")
        }

    }
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when(val item = getItem(position)){
            is Ad -> (holder as? AdViewHolder)?.bind(item)
            is Post -> (holder as PostViewHolder).bind(item)
            null -> error("unknown item type")
        }
    }
}

class AdViewHolder(
    private val binding: CardAdBinding,

    ) : RecyclerView.ViewHolder(binding.root) {
    fun bind(ad: Ad) {

        binding.image.loading(UrlProvider.getMediaUrl(ad.image))
    }
}

class PostViewHolder(
    private val binding: CardPostBinding,

    private val onInteractionListener: OnInteractionListener
) : RecyclerView.ViewHolder(binding.root) {
    fun bind(post: Post) {
        binding.apply {
            author.text = post.author
            published.text = post.published //
            content.text = post.content
            avatar.load(UrlProvider.getAvatarUrl(post.authorAvatar))
            repost.text = formatNumber(post.reposts)
            val imageUrl = post.attachment?.url
            Log.d("imageURL", "$imageUrl")
            if (imageUrl != null) {
                val yy = UrlProvider.getMediaUrl(imageUrl)
                photo.loading(yy)
                photo.visibility = View.VISIBLE
            } else
                photo.visibility = View.GONE

            like.isChecked = post.likedByMe
            like.text = formatNumber(post.likes)
            like.setOnClickListener {
                like.isChecked = !like.isChecked
                onInteractionListener.onLike(post)
            }
            repost.setOnClickListener {
                onInteractionListener.onRepost(post)

            }
            photo.setOnClickListener {
                onInteractionListener.onPhoto(post)
            }

            preview.setOnClickListener {
                onInteractionListener.onVideo(post)
            }
            buttonPlay.setOnClickListener {
                onInteractionListener.onVideo(post)
            }
            root.setOnClickListener {
                onInteractionListener.onPost(post)
            }
            val con = post.linkVideo
            if (!con.isNullOrBlank()) {
                binding.videoContainer.visibility = View.VISIBLE
            } else binding.videoContainer.visibility = View.GONE
            menu.isVisible = post.ownedByMe
            menu.setOnClickListener {
                PopupMenu(it.context, it).apply {
                    inflate(R.menu.options_post)
                    setOnMenuItemClickListener { item ->
                        when (item.itemId) {
                            R.id.remove -> {
                                onInteractionListener.onRemove(post)

                                true
                            }

                            R.id.edit -> {

                                onInteractionListener.onEdit(post)
                                true
                            }

                            else -> false
                        }
                    }
                }.show()
            }
        }
    }
}

object PostDiffCallback : DiffUtil.ItemCallback<FeedItem>() {
    override fun areItemsTheSame(oldItem: FeedItem, newItem: FeedItem): Boolean {
        if (oldItem::class != newItem::class) {
            return false
        }
        return oldItem.id == newItem.id
    }

    override fun areContentsTheSame(oldItem: FeedItem, newItem: FeedItem) = oldItem == newItem

}
