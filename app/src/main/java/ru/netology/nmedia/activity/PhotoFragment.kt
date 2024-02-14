package ru.netology.nmedia.activity


import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dagger.hilt.android.AndroidEntryPoint
import ru.netology.nmedia.databinding.FragmentPhotoBinding
import ru.netology.nmedia.image.loading
import ru.netology.nmedia.url.UrlProvider
import ru.netology.nmedia.util.StringArg

@AndroidEntryPoint
class PhotoFragment : Fragment() {
    companion object {
        var Bundle.hhh: String? by StringArg
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentPhotoBinding.inflate(
            inflater,
            container,
            false
        )

        this.arguments?.hhh?.let { uriString ->
            val fullUri = UrlProvider.getMediaUrl(uriString)
            binding.photo.loading(fullUri)
        }

        return binding.root
    }
}
