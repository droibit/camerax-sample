package com.github.droibit.sample.camerax.ui.gallery

import android.content.Context
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import coil.api.load
import coil.size.Scale
import com.github.droibit.sample.camerax.R
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.android.synthetic.main.list_item_photo.view.*

class GalleryFragment : Fragment(R.layout.fragment_gallery) {

    private val args: GalleryFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view_pager.apply {
            adapter = PhotoListAdapter(requireContext(), args.photoUris.toList())
            orientation = ViewPager2.ORIENTATION_HORIZONTAL
            offscreenPageLimit = 1
        }
    }
}

class PhotoListAdapter(
    context: Context,
    private val photoUris: List<Uri>
): RecyclerView.Adapter<PhotoListAdapter.ViewHolder>() {

    private val layoutInflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView = layoutInflater.inflate(R.layout.list_item_photo, parent, false)
        return ViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.update(photoUris[position])
    }

    override fun getItemCount(): Int = photoUris.count()

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        private val photoView: ImageView = itemView.photo

        fun update(photoUri: Uri) {
            photoView.load(photoUri) {
                scale(Scale.FIT)
                crossfade(200)
            }
        }
    }
}