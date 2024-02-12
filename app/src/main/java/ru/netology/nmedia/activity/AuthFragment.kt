package ru.netology.nmedia.activity

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import ru.netology.nmedia.R
import ru.netology.nmedia.databinding.FragmentAuthBinding
import ru.netology.nmedia.util.AndroidUtils
import ru.netology.nmedia.viewmodel.AuthFragmentViewModel


class AuthFragment : Fragment() {
    private val viewModel: AuthFragmentViewModel by viewModels()
    private var name: String = ""
    private var pass: String = ""
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val binding = FragmentAuthBinding.inflate(
            inflater,
            container,
            false
        )

        binding.username.addTextChangedListener {
            name = binding.username.text.toString()
            binding.login.isEnabled = name.isNotBlank() && pass.isNotBlank()
        }
        binding.password.addTextChangedListener {
            pass = binding.password.text.toString()
            binding.login.isEnabled = name.isNotBlank() && pass.isNotBlank()
        }

        binding.login.setOnClickListener {
            lifecycleScope.launch {
                AndroidUtils.hideKeyboard(requireView())
                viewModel.signIn(name, pass)
                viewModel.state.observe(viewLifecycleOwner) {
                    if (it.error) {
                        Snackbar.make(binding.root, R.string.authorization_error,Snackbar.LENGTH_LONG)
                            .show()

                    }
                }
                findNavController().navigateUp()

            }

        }


            return binding.root
        }


    }
