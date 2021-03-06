package com.oucs.mystores.ui.menu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.oucs.mystores.R
import com.oucs.mystores.common.TypeError
import com.oucs.mystores.databinding.FragmentMenuBinding
import com.oucs.mystores.model.StoreViewModel
import com.oucs.mystores.model.entities.Store

class StoreFragment : Fragment(), StoreAdapter.OnClickItem {
    private var resultBinding: FragmentMenuBinding? = null
    private val binding get() = resultBinding!!
    private lateinit var storeAdapter: StoreAdapter
    private lateinit var viewModel: StoreViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(StoreViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View{
        resultBinding = FragmentMenuBinding.inflate(layoutInflater,container,false)
        return binding.root
    }
    override fun onDestroyView() {
        super.onDestroyView()
        resultBinding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initMenu()
        binding.addStoreFab.setOnClickListener {
            NavHostFragment
                .findNavController(this)
                .navigate(R.id.action_menu_to_edit_store)
        }
    }

    private fun initMenu() {
        storeAdapter = StoreAdapter(this@StoreFragment)
        val column = requireActivity().resources.getInteger(R.integer.count_column)
        binding.listStores.apply {
            layoutManager = GridLayoutManager(requireContext(),column)
            adapter = storeAdapter
        }
        viewModel.getStores().observe(viewLifecycleOwner){ stores ->
            storeAdapter.submitList(stores)
            binding.progressBar.visibility = View.GONE
        }
        viewModel.getTypeError().observe(viewLifecycleOwner){
            val messageError = when(it){
                TypeError.GET -> R.string.get_error
                TypeError.INSERT -> R.string.insert_error
                TypeError.UPDATE -> R.string.update_error
                TypeError.DELETE -> R.string.delete_error
                else -> R.string.unknown_error
            }
            Snackbar.make(binding.root,messageError,Snackbar.LENGTH_SHORT).show()
        }
        viewModel.getProgress().observe(viewLifecycleOwner){
            binding.progressBar.visibility = if (it) View.VISIBLE else View.INVISIBLE
        }
    }
    override fun onClickStore(storeId:Long) {
        val actionToEditStore = StoreFragmentDirections.actionMenuToEditStore(storeId)
        NavHostFragment
            .findNavController(this)
            .navigate(actionToEditStore)
    }

    override fun onClickMyFavoriteStore(store: Store) {
        viewModel.updateStore(store)
    }
    override fun onDoubleClickStore(store: Store) {
        val items = requireActivity()
            .resources
            .getStringArray(R.array.options_for_item)
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.choose_option)
            .setItems(items){ _, item ->
                when(item){
                    0 -> callStore(store.phone)
                    1 -> webStore(store.webSite)
                    else -> confirmDelete(store)
                }
            }
            .show()
    }
    private fun callStore(phone:String?){
        if (!phone.isNullOrEmpty()){
            startIntentService(Intent.ACTION_DIAL,"tel:+$phone")
        }
        else{
            Toast.makeText(requireContext(),"Not such phone",Toast.LENGTH_SHORT).show()
        }
    }
    private fun webStore(web:String?){
        if (!web.isNullOrEmpty()){
            startIntentService(Intent.ACTION_VIEW,web)
        }
        else{
            Toast.makeText(requireContext(),"Not such website",Toast.LENGTH_SHORT).show()
        }
    }
    private fun startIntentService(actionIntent:String,uriPArse:String){
        val webIntent = Intent().apply{
            action = actionIntent
            data = Uri.parse(uriPArse)
        }
        //this action require <queries> label in AndroidManifest
        if (webIntent.resolveActivity(requireActivity().packageManager) != null){
            startActivity(webIntent)
        }
        else{
            Toast.makeText(requireContext(),"Not such app for this action",Toast.LENGTH_SHORT).show()
        }
    }
    private fun confirmDelete(store: Store){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.title_for_delete_store)
            .setPositiveButton(R.string.to_delete) { _, _ ->
                viewModel.deleteStore(store)
            }
            .setNegativeButton(R.string.to_cancel,null)
            .show()
    }
}