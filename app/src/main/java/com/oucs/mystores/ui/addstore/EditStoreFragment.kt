package com.oucs.mystores.ui.addstore

import android.content.Context
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.webkit.URLUtil
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.navArgs
import coil.load
import coil.transform.CircleCropTransformation
import com.google.android.material.textfield.TextInputLayout
import com.oucs.mystores.ui.MainActivity
import com.oucs.mystores.R
import com.oucs.mystores.databinding.FragmentEditStoreBinding
import com.oucs.mystores.model.EditStoreViewModel
import com.oucs.mystores.model.entities.Store
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditStoreFragment : Fragment() {
    private var resultBinding:FragmentEditStoreBinding? = null
    private val binding get() = resultBinding!!
    private val args:EditStoreFragmentArgs by navArgs()
    private val coroutineScope = CoroutineScope(Dispatchers.Main.immediate)
    private var idStore=-1L
    private lateinit var viewModel: EditStoreViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(EditStoreViewModel::class.java)
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        resultBinding = FragmentEditStoreBinding.inflate(inflater,container,false)
        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        resultBinding = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupAppBar()
        binding.nameStore.addTextChangedListener{
            if (binding.nameLayout.error != null){
                binding.nameLayout.error = null
            }
        }
        binding.imagePageStore.addTextChangedListener{
            coroutineScope.launch {
                if (binding.imagePageLayout.error != null){
                    binding.imagePageLayout.error = null
                }
                withContext(Dispatchers.Default){
                    val imageURL = binding.imagePageStore.text.toString()
                    if (imageURL.isNotEmpty()){
                        if (URLUtil.isValidUrl(imageURL)){
                            binding.imageCharged.load(imageURL){
                                crossfade(false)
                                placeholder(R.drawable.ic_image)
                                transformations(CircleCropTransformation())
                            }
                        }
                    }
                }
            }
        }
        binding.webPageStore.addTextChangedListener {
            if (binding.webPageLayout.error != null){
                binding.webPageLayout.error = null
            }
        }
    }

    private fun setupAppBar() {
        //add menu to appbar
        setHasOptionsMenu(true)
        //get args
        idStore = args.idStore
        if (idStore != -1L){
            //if args is not equal -1
            chargeStoreData(idStore)
            (activity as? MainActivity)?.supportActionBar?.title = getString(R.string.edit_store)
        }
    }

    private fun chargeStoreData(id:Long) {
        viewModel.getStore(id).observe(viewLifecycleOwner){
            if (it!=null){
                binding.apply {
                    nameStore.setText(it.name)
                    webPageStore.setText(it.webSite)
                    imagePageStore.setText(it.imageURL)
                    phoneStore.setText(it.phone)
                }
            }
            else{
                Toast.makeText(requireActivity(),"Store Not found",Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_store,menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        //action for to press back home button
        return when (item.itemId){
            android.R.id.home -> {
                 backToHome()
                true
            }
            R.id.action_save_store -> {
                if (validation()){
                    saveStore()
                    Toast.makeText(requireContext(),
                        R.string.action_save_success,
                        Toast.LENGTH_LONG).show()
                    backToHome()
                }
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
    private fun validation(vararg textLayouts:TextInputLayout): Boolean {
        var isValid = true
        for (textLayout in textLayouts){
            if (textLayout.editText?.text.toString().trim().isEmpty()){
                textLayout.error = getString(R.string.required_field)
                isValid = false
            }
        }
        return isValid
    }
    private fun validateURL(vararg textLayouts:TextInputLayout): Boolean {
        var isValid = true
        for (textLayout in textLayouts){
            val textToURL = textLayout.editText?.text.toString().trim()
            if (textToURL.isNotEmpty()){
                if (!URLUtil.isValidUrl(textToURL)){
                    textLayout.error = getString(R.string.url_not_found)
                    isValid = false
                }
            }
        }
        return isValid
    }

    private fun validation(): Boolean {
        return validation(binding.nameLayout,binding.imagePageLayout)
                &&validateURL(binding.webPageLayout,binding.imagePageLayout)
    }
    private fun saveStore() {
        hideKeyword()
        val store = Store(
            name = binding.nameStore.text.toString().trim(),
            phone = binding.phoneStore.text.toString().trim(),
            webSite = binding.webPageStore.text.toString().trim(),
            imageURL = binding.imagePageStore.text.toString().trim()
        )
        viewModel.saveStore(idStore,store)
    }
    private fun hideKeyword(){
        val view:View? = activity?.currentFocus
        if (view != null){
            (activity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .hideSoftInputFromWindow(view.windowToken,0)
        }
    }
    private fun backToHome(){
        NavHostFragment
            .findNavController(this)
            .navigate(R.id.action_edit_store_to_menu)
    }
}