package com.yandex.navigationdemo.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.yandex.navigationdemo.ui.R
import com.yandex.navigationdemo.ui.databinding.FragmentSettingsBinding
import com.yandex.navigationdemo.ui.settings.settingslist.SettingsItemsMapper
import com.yandex.navigationdemo.ui.settings.settingslist.SettingsListAdapterFactory
import com.yandex.navigationdemo.ui.settings.settingslist.SettingsScreen
import com.yandex.navigationdemo.ui.settings.settingslist.toScreenName
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SettingsFragment: Fragment(R.layout.fragment_settings) {

    private lateinit var binding: FragmentSettingsBinding

    @Inject
    lateinit var settingsItemsMapper: SettingsItemsMapper

    @Inject
    lateinit var settingsListAdapterFactory: SettingsListAdapterFactory

    private val settingsAdapter by lazy { settingsListAdapterFactory.create(::openSettings) }

    private val args: SettingsFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSettingsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.apply {
            viewSettingsRecycle.adapter = settingsAdapter
            textSettingScreenTitle.text = args.screen.toScreenName(view.context)
            buttonCloseSettings.setOnClickListener { findNavController().popBackStack() }
        }

        settingsAdapter.updateItems(settingsItemsMapper.items(args.screen))
    }

    private fun openSettings(screen: SettingsScreen) {
        val action = SettingsFragmentDirections.actionGlobalSettingsFragment(screen)
        findNavController().navigate(action)
    }
}
