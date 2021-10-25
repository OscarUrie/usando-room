package com.oucs.mystores.ui.menu

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.oucs.mystores.R
import com.oucs.mystores.common.StoreApplication
import com.oucs.mystores.databinding.FragmentMenuBinding
import com.oucs.mystores.model.Store
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MenuFragment : Fragment(), StoreAdapter.OnClickItem {
    private var resultBinding: FragmentMenuBinding? = null
    private val binding get() = resultBinding!!
    private lateinit var storeAdapter: StoreAdapter
    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
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
        val storeList:MutableList<Store> = ArrayList()
        coroutineScope.launch {
            storeList.addAll(getStoreList())
            storeAdapter = StoreAdapter(storeList,this@MenuFragment)
            val column = requireActivity().resources.getInteger(R.integer.count_column)
            binding.listStores.apply {
                layoutManager = GridLayoutManager(requireContext(),column)
                adapter = storeAdapter
            }
        }
    }
    private suspend fun getStoreList():List<Store>{
        return withContext(Dispatchers.Default){
            StoreApplication.db.storeDao().getStoreList()
        }
    }

    override fun onClickStore(storeId:Long) {
        val actionToEditStore = MenuFragmentDirections.actionMenuToEditStore(storeId)
        NavHostFragment
            .findNavController(this)
            .navigate(actionToEditStore)
    }

    override fun onClickMyFavoriteStore(store: Store) {
        coroutineScope.launch {
            withContext(Dispatchers.Default){
                StoreApplication.db.storeDao().updateStore(store)
            }
        }
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
            val callIntent = Intent().apply{
                action = Intent.ACTION_DIAL
                data = Uri.parse("tel:$phone")
            }
            //this action require <queries> label in AndroidManifest
            if (callIntent.resolveActivity(requireActivity().packageManager) != null){
                startActivity(callIntent)
            }
            else{
                Toast.makeText(requireContext(),"Not such app for this action",Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(requireContext(),"Not such phone",Toast.LENGTH_SHORT).show()
        }
    }
    private fun webStore(web:String?){
        if (!web.isNullOrEmpty()){
            val webIntent = Intent().apply{
                action = Intent.ACTION_VIEW
                data = Uri.parse(web)
            }
            //this action require <queries> label in AndroidManifest
            if (webIntent.resolveActivity(requireActivity().packageManager) != null){
                startActivity(webIntent)
            }
            else{
                Toast.makeText(requireContext(),"Not such app for this action",Toast.LENGTH_SHORT).show()
            }
        }
        else{
            Toast.makeText(requireContext(),"Not such website",Toast.LENGTH_SHORT).show()
        }
    }
    private fun confirmDelete(store: Store){
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(R.string.title_for_delete_store)
            .setPositiveButton(R.string.to_delete) { _, _ ->
                coroutineScope.launch {
                    withContext(Dispatchers.Default){
                        StoreApplication.db.storeDao().deleteStore(store)
                    }
                    storeAdapter.deleteStore(store)
                }
            }
            .setNegativeButton(R.string.to_cancel,null)
            .show()
    }
}