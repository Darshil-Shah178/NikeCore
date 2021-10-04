package com.example.nikecore.ui.ar

import android.content.ContentValues
import android.graphics.Point
import android.net.Uri
import androidx.lifecycle.ViewModelProvider
import android.os.Bundle
import android.os.StrictMode
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.example.nikecore.R
import com.google.ar.core.HitResult
import com.google.ar.core.Plane
import com.google.ar.sceneform.AnchorNode
import com.google.ar.sceneform.math.Vector3
import com.google.ar.sceneform.rendering.ModelRenderable
import com.google.ar.sceneform.rendering.ViewRenderable
import com.google.ar.sceneform.ux.ArFragment
import com.google.ar.sceneform.ux.TransformableNode
import kotlinx.android.synthetic.main.a_r_fragment.*
import kotlinx.android.synthetic.main.a_r_fragment.view.*

class ARFragment : Fragment() {

    private lateinit var arFrag: ArFragment
    private var viewRenderable: ViewRenderable? = null
    private var modelRenderable: ModelRenderable? = null





    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.a_r_fragment, container, false)

        arFrag = childFragmentManager.findFragmentById(
            R.id.sceneform_fragment
        ) as ArFragment

        view.findViewById<Button>(R.id.addBtn).setOnClickListener {
            add3dObject()
        }


        StrictMode.setThreadPolicy(
            StrictMode.ThreadPolicy.Builder()
                .permitAll().build())
        // (CC BY 4.0) Donated by Cesium for glTF testing.
        ModelRenderable.builder()
            .setSource(requireContext(), Uri.parse("file:///android_asset/ticket3.gltf"))
            .setIsFilamentGltf(true)
            .setAsyncLoadEnabled(true)
            .setRegistryId("CesiumMan")
            .build()
            .thenAccept { modelRenderable = it }
            .exceptionally {
                Log.e(ContentValues.TAG, "something went wrong ${it.localizedMessage}")
                null
            }

        arFrag.setOnTapArPlaneListener { hitResult: HitResult?, _, _ ->
            viewRenderable ?: return@setOnTapArPlaneListener
            //Creates a new anchor at the hit location
            val anchor = hitResult!!.createAnchor()
            //Creates a new anchorNode attaching it to anchor
            val anchorNode = AnchorNode(anchor)
            // Add anchorNode as root scene node's child
            anchorNode.setParent(arFrag.arSceneView.scene)
            // Can be selected, rotated...
            val viewNode = TransformableNode(arFrag.transformationSystem)
            viewNode.renderable = viewRenderable
            // Add viewNode as anchorNode's child
            viewNode.setParent(anchorNode)
            // Sets this as the selected node in the TransformationSystem
            viewNode.select()

        }

        return view
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


    }

    private fun getScreenCenter(): Point {
        // find the root view of the activity
        val vw = view
        // returns center of the screen as a Point object
        return Point(vw!!.width / 2, vw.height / 2)
    }

    private fun add3dObject() {
        val frame = arFrag.arSceneView.arFrame
        if (frame != null && modelRenderable != null) {
            view?.findViewById<Button>(R.id.addBtn)?.visibility = View.GONE
            val pt = getScreenCenter()
            val hits = frame.hitTest(pt.x.toFloat(), pt.y.toFloat())
            for (hit in hits) {
                val trackable = hit.trackable
                if (trackable is Plane) {
                    val anchor = hit!!.createAnchor()
                    val anchorNode = AnchorNode(anchor)
                    anchorNode.setParent(arFrag.arSceneView.scene)
                    val mNode =
                        TransformableNode(arFrag.transformationSystem)
                    mNode.setOnTapListener { hitTestResult, motionEvent ->
                        view?.findViewById<Button>(R.id.addBtn)?.visibility = View.VISIBLE
                    }
                    mNode.renderable = modelRenderable
                    mNode.scaleController.minScale = 0.4f
                    mNode.scaleController.maxScale = 2.0f
                    mNode.localScale = Vector3(0.2f, 0.2f, 0.2f)
                    mNode.setParent(anchorNode)
                    mNode.select()
                    break
                }
            }
        }
    }

}