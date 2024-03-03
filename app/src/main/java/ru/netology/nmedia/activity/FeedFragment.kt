package ru.netology.nmedia.activity

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.paging.LoadState
import androidx.recyclerview.widget.RecyclerView.AdapterDataObserver
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.activity.NewPostFragment.Companion.textArg
import ru.netology.nmedia.activity.PhotoFragment.Companion.hhh
import ru.netology.nmedia.activity.PostFragment.Companion.postId
import ru.netology.nmedia.adapter.OnInteractionListener
import ru.netology.nmedia.adapter.PostsAdapter
import ru.netology.nmedia.databinding.FragmentFeedBinding
import ru.netology.nmedia.dto.Post
import ru.netology.nmedia.viewmodel.PostViewModel

@AndroidEntryPoint
class FeedFragment : Fragment() {

    private val viewModel: PostViewModel by activityViewModels()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentFeedBinding.inflate(
            inflater,
            container,
            false
        )

        val adapter = PostsAdapter(object : OnInteractionListener {
            override fun onLike(post: Post) {
                viewModel.likeById(post)

            }

            override fun onEdit(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_editPostFragment,
                    Bundle().apply { textArg = post.content })
                viewModel.edit(post)


            }

            override fun onRemove(post: Post) {
                viewModel.removeById(post.id)
            }

            override fun onRepost(post: Post) {
                val intent = Intent().apply {
                    action = Intent.ACTION_SEND
                    putExtra(Intent.EXTRA_TEXT, post.content)
                    type = "text/plain"
                }
                val shareIntent =
                    Intent.createChooser(intent, getString(R.string.chooser_share_post))
                startActivity(shareIntent)
                viewModel.repostById(post.id)
            }

            override fun onVideo(post: Post) {
                val url = Uri.parse(post.linkVideo)
                val intent = Intent(Intent.ACTION_VIEW, url)
                startActivity(intent)
            }

            override fun onPost(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_postFragment,
                    Bundle().apply { postId = post.id })
            }

            override fun onPhoto(post: Post) {
                findNavController().navigate(
                    R.id.action_feedFragment_to_photoFragment,
                    Bundle().apply { hhh = post.attachment?.url.toString() }
                )
            }

        })




        binding.list.adapter = adapter

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.data.collectLatest(adapter :: submitData)
            }
        }
//        viewModel.data.observe(viewLifecycleOwner) { state ->
//            adapter.submitList(state.posts)
//            binding.emptyText.isVisible = state.empty
//        }
//        viewModel.state.observe(viewLifecycleOwner) { state ->
//            binding.progress.isVisible = state.loading
//            binding.swipeRefresh.isRefreshing = state.refreshing
//            if (state.error) {
//                Snackbar.make(binding.root, R.string.error_loading, Snackbar.LENGTH_LONG)
//                    .setAction(R.string.retry_loading) {
//                        viewModel.loadPosts()
//                    }.show()
//            }
//
//        }
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                adapter.loadStateFlow.collectLatest { state ->
                    binding.swipeRefresh.isRefreshing =
                        state.refresh is LoadState.Loading ||
                                state.prepend is LoadState.Loading ||
                                state.append is LoadState.Loading
                }
            }
        }
        binding.swipeRefresh.setOnRefreshListener {
            viewModel.refresh()
        }
        binding.retryButton.setOnClickListener {
            viewModel.loadPosts()
        }
        binding.add.setOnClickListener {
            findNavController().navigate(R.id.action_feedFragment_to_newPostFragment)
        }

        adapter.registerAdapterDataObserver(object : AdapterDataObserver() {
            override fun onItemRangeInserted(positionStart: Int, itemCount: Int) {
                if (positionStart == 0) {
                    binding.refreshButton.setOnClickListener {
                        viewModel.readdd()
                        binding.list.smoothScrollToPosition(0)
                        binding.refreshButton.visibility = View.GONE

                    }
                }
            }
        })
//        viewModel.newerCount.observe(viewLifecycleOwner) {
//            if (it >= 1) {
//                binding.refreshButton.visibility = View.VISIBLE
//            }
//            Log.d("FeedFragment", "Newer count : $it")
//        }



        return binding.root


    }


}
