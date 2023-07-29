package com.yandex.mapkitdemo.search.ui.details

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.yandex.mapkitdemo.search.data.goneOrRun
import com.yandex.mapkitdemo.search.databinding.LayoutDetailsDialogBinding

class DetailsDialogFragment : DialogFragment() {
    private lateinit var binding: LayoutDetailsDialogBinding

    private val viewModel: DetailsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = LayoutDetailsDialogBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.uiState()?.let {
            binding.apply {
                textTitle.text = it.title
                textSubtitle.text = it.descriptionText
                textPlace.text = "${it.location?.latitude}, ${it.location?.longitude}"
                textUri.goneOrRun(it.uri) {
                    text = it
                }

                when (val state = it.typeSpecificState) {
                    is TypeSpecificState.Business -> {
                        layoutBusinessInfo.isVisible = true
                        textType.text = "Business organisation:"
                        textBusinessName.text = state.name
                        textBusinessWorkingHours.goneOrRun(state.workingHours) {
                            text = it
                        }
                        textBusinessCategories.text = state.categories
                        textBusinessPhones.text = state.phones
                        textBusinessLinks.goneOrRun(state.link) {
                            text = it
                        }
                    }
                    is TypeSpecificState.Toponym -> {
                        layoutToponymInfo.isVisible = true
                        textType.text = "Toponym:"
                        textToponymAddress.text = state.address
                    }
                    TypeSpecificState.Undefined -> {
                        textType.isVisible = false
                    }
                }
            }
        }
    }
}
