package ru.netology.nmedia.activity


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel

class NewPostFragment : Fragment() {
    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by viewModels(
        ownerProducer = ::requireParentFragment
    )

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentNewPostBinding.inflate(
            inflater,
            container,
            false
        )
        viewModel.data.observe(viewLifecycleOwner) { state ->
            if (state.errorCRUD) {
                showSnackbar()

            }
        }

        this.arguments?.textArg
            ?.let(binding.content::setText)

        binding.ok.setOnClickListener {
            val text = binding.content.text.toString()
            if (text.isNotBlank()){
                viewModel.changeContent(text)
                viewModel.save()
            }
            AndroidUtils.hideKeyboard(requireView())
        }
        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.loadPosts()
            findNavController().navigateUp()
        }
        return binding.root
    }
    private fun showSnackbar() {
        Snackbar.make(requireView(), R.string.error_message, Snackbar.LENGTH_SHORT).show()
    }
}
