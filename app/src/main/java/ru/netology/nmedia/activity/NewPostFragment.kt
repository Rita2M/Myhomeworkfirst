package ru.netology.nmedia.activity


import android.app.Activity
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.net.toFile
import androidx.core.view.MenuProvider
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.github.dhaval2404.imagepicker.ImagePicker
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentNewPostBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.util.StringArg
import ru.netology.nmedia.viewmodel.PostViewModel
@AndroidEntryPoint
class NewPostFragment : Fragment() {
    companion object {
        var Bundle.textArg: String? by StringArg
    }

    private val viewModel: PostViewModel by activityViewModels()
    private val photoResultContract =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            if (it.resultCode == Activity.RESULT_OK) {
                val uri = it.data?.data ?: return@registerForActivityResult
                val file = uri.toFile()
                viewModel.setPhoto(uri, file)
            }
        }
    private var fragmentBinding: FragmentNewPostBinding? = null


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
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.save_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean =
                when (menuItem.itemId) {
                    R.id.save -> {
                        viewModel.changeContent(binding.edit.text.toString())
                        viewModel.save()
                       // viewModel.data
                        AndroidUtils.hideKeyboard(requireView())
                        true
                    }

                    else -> false
                }


        }, viewLifecycleOwner)



        fragmentBinding = binding
//        val pickPhotoLauncher =
//            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//                when (it.resultCode) {
//                    ImagePicker.RESULT_ERROR -> {
//                        Snackbar.make(
//                            binding.root,
//                            ImagePicker.getError(it.data),
//                            Snackbar.LENGTH_LONG
//                        ).show()
//                    }
//
//                    Activity.RESULT_OK -> {
//                        val uri: Uri = it.data?.data ?: return@registerForActivityResult
//                        viewModel.changePhoto(uri, uri.toFile())
//                    }
//                }
//            }


        arguments?.textArg
            ?.let(binding.edit::setText)
        binding.edit.requestFocus()


        viewModel.photo.observe(viewLifecycleOwner) {
            if (it == null) {
                binding.photoContainer.isGone = true
                return@observe
            }
            binding.photoContainer.isVisible = true
            binding.photo.setImageURI(it.uri)
        }
        binding.removePhoto.setOnClickListener {
            viewModel.clearPhoto()
        }

        viewModel.postCreated.observe(viewLifecycleOwner) {
            viewModel.data
            findNavController().navigateUp()
        }

        binding.gallery.setOnClickListener {
            ImagePicker.Builder(this)
                .crop()
                .galleryOnly()
                .maxResultSize(2048, 2048)
                .createIntent(photoResultContract::launch)

        }
        binding.takePhoto.setOnClickListener {
            ImagePicker.Builder(this)
                .crop()
                .cameraOnly()
                .maxResultSize(2048, 2048)
                .createIntent(photoResultContract::launch)

        }

        return binding.root
    }

}
