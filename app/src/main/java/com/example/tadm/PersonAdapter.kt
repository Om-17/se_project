package com.example.tadm

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.tadm.model.PersonDetail
import com.squareup.picasso.Picasso
import com.squareup.picasso.Transformation
import android.graphics.Bitmap
import android.view.Gravity
import com.example.tadm.model.FamilyDetail
import com.google.android.material.button.MaterialButton
import com.google.android.material.resources.MaterialResources.getDimensionPixelSize


class PersonAdapter(private val context: Context, private val personList: List<PersonDetail>) :
    RecyclerView.Adapter<PersonAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.expandable_card_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val person = personList[position]

        // Bind data to views in the CardView item
        holder.nameTextView.text = person.d_name
        holder.fatherNameTextView.text = person.d_fathername
        holder.mobNoTextView.text=person.d_mobno.toString()

        holder.expandButton.setOnClickListener {
            holder.toggleExpandableLayout()
        }
        println(!person.d_picurl.isNullOrEmpty())
        println(person.d_picurl)
        if (!person.d_picurl.isNullOrEmpty()) {
            // Load image using Picasso
            Picasso.get()
                .load(person.d_picurl)
                .placeholder(R.drawable.baseline_person)
                .transform(CircleTransformation())
                .into(holder.imgView)
        }else{
            Picasso.get()
                .load(R.drawable.baseline_person)
                .placeholder(R.drawable.baseline_person)
                .error(R.drawable.baseline_person)
                .transform(CircleTransformation())
                .into(holder.imgView)
        }
        // Set initial state of expandable layout based on a flag in your PersonDetail
        if (person.isExpanded) {
            holder.expandableLayout.visibility = View.VISIBLE
            holder.expandButton.setImageResource(R.drawable.baseline_expand_more_24)
        } else {
            holder.expandableLayout.visibility = View.GONE
            holder.expandButton.setImageResource(R.drawable.baseline_expand_less_24)

        }
    }

    override fun getItemCount(): Int {
        return personList.size
    }

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val nameTextView: TextView = itemView.findViewById(R.id.nameTextView)
        val imgView:ImageView=itemView.findViewById(R.id.img)

        val fatherNameTextView: TextView = itemView.findViewById(R.id.fatherNameTextView)
        val mobNoTextView: TextView = itemView.findViewById(R.id.mobNoTextView)
        val expandableLayout: LinearLayout = itemView.findViewById(R.id.expandableLayout)
        // Initialize other views here as needed
        val expandButton: ImageButton = itemView.findViewById(R.id.expandButton)
        val show_main:LinearLayout=itemView.findViewById(R.id.show_main)



        init {
            expandButton.setOnClickListener {
                toggleExpandableLayout()
            }
            show_main.setOnClickListener {
                toggleExpandableLayout()
            }
//            val familyBtn = MaterialButton(itemView.context)
//            familyBtn.text = "Delete"
//            familyBtn.layoutParams = LinearLayout.LayoutParams(
//                LinearLayout.LayoutParams.WRAP_CONTENT,
//                LinearLayout.LayoutParams.WRAP_CONTENT
//            ).apply {
//
//                gravity = Gravity.CENTER
//                marginStart =getDimensionPixelSize(R.dimen.margin_start)
//                marginEnd= getDimensionPixelSize(R.dimen.margin_start)
//
//                gravity = Gravity.END
//
//            }
        }
        fun bind(person: PersonDetail) {
            nameTextView.text = person.d_name

            if (!person.d_picurl.isNullOrEmpty()) {
                // Load image using Picasso
                Picasso.get()
                    .load(person.d_picurl)
                    .placeholder(R.drawable.circle_shape)
                    .error(R.drawable.baseline_person)
                    .transform(CircleTransformation())
                    .into(imgView)
            }else{
                Picasso.get()
                    .load(R.drawable.baseline_person)
                    .placeholder(R.drawable.baseline_person)
                    .transform(CircleTransformation())
                    .into(imgView)
            }

            // Bind other data as needed
        }


        fun toggleExpandableLayout() {
            if (expandableLayout.visibility == View.VISIBLE) {
                expandableLayout.visibility = View.GONE
                personList[adapterPosition].isExpanded = false
                expandButton.setImageResource(R.drawable.baseline_expand_less_24)

            } else {
                expandableLayout.visibility = View.VISIBLE
                personList[adapterPosition].isExpanded = true
                expandButton.setImageResource(R.drawable.baseline_expand_more_24)
                // Add dynamic views here
                if (expandableLayout.childCount == 0) {
                    // Add dynamic views here only if they are not already added
                    val personDetail = personList[adapterPosition]

                    // Add views for each data field
                    addTextView("ID ", personDetail.d_id)
                    addTextView("Address ", personDetail.d_address)
                    addTextView("Religion ", personDetail.d_religion)
                    addTextView("Age ", personDetail.d_age)
                    addTextView("Marital Status ", personDetail.d_maritalstatus)
                    addTextView("Destination ", personDetail.d_destination)
                    addTextView("Coming From  ", personDetail.d_routeuse)
                    addTextView("Aadhar No ", personDetail.d_duration)
                    addTextView("Date Of Registration ", personDetail.d_placevislastyear)
                    personDetail.d_familydeatils.let {
                        if (it != null) {
                            addFamilydetails(it)
                        }
                    }
                    // Add views for d_deradetails map if needed
                    personDetail.d_deradetails?.let { addDeradetailsViews(it) }

                }
            }
        }
        private fun addTextView(label: String, value: String) {
            val textView = TextView(itemView.context)
            textView.layoutParams = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )
            textView.text = "$label: $value"
            textView.textSize = 16f
            textView.setPadding(0, 7.dpToPx(itemView.context), 0, 0)
            expandableLayout.addView(textView)
        }
        private fun addFamilydetails(familyDetail: List<FamilyDetail>) {
            for (detail in familyDetail) {
                val textView = TextView(itemView.context)
                textView.layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
                val label = "Family Detail:"
                val value = "\n Name: ${detail.name}\n Age: ${detail.age}\n Gender: ${detail.gender}\n " +
                        "Relation: ${detail.relation}\n Aadhar No: ${detail.aadhar_no}\n " +
                        "Mobile No: ${detail.mobile_no}\n"
                textView.text = "$label $value"
                textView.textSize = 16f
                textView.setPadding(0, 7.dpToPx(itemView.context), 0, 0)
                expandableLayout.addView(textView)
            }
        }

        private fun addDeradetailsViews(deradetails: Map<String, String>) {
            for ((key, value) in deradetails) {
                addTextView(key, value)
            }
        }
    }


    fun Int.dpToPx(context: Context): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }



}


class CircleTransformation : Transformation {
    override fun key(): String {
        return "circle"
    }

    override fun transform(source: Bitmap): Bitmap {
        val size = Math.min(source.width, source.height)
        val x = (source.width - size) / 2
        val y = (source.height - size) / 2
        val squaredBitmap = Bitmap.createBitmap(source, x, y, size, size)
        if (squaredBitmap != source) {
            source.recycle()
        }
        val bitmap = Bitmap.createBitmap(size, size, source.config)
        val canvas = android.graphics.Canvas(bitmap)
        val paint = android.graphics.Paint()
        val shader = android.graphics.BitmapShader(
            squaredBitmap,
            android.graphics.Shader.TileMode.CLAMP,
            android.graphics.Shader.TileMode.CLAMP
        )
        paint.shader = shader
        paint.isAntiAlias = true
        val radius = size / 2f
        canvas.drawCircle(radius, radius, radius, paint)
        squaredBitmap.recycle()
        return bitmap
    }
}

