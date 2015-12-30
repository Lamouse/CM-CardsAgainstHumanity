package com.example.asus.cardsagainsthumanity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.net.wifi.p2p.WifiP2pDevice;
import android.os.Bundle;
import android.app.Fragment;
import android.support.v4.app.ListFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.asus.cardsagainsthumanity.router.AllEncompasingP2PClient;
import com.example.asus.cardsagainsthumanity.router.MeshNetworkManager;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link RoomPeersList.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RoomPeersList#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RoomPeersList extends ListFragment
{
    public List<AllEncompasingP2PClient> peers = new ArrayList<>();
    ProgressDialog progressDialog = null;
    View mContentView = null;

    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RoomPeersFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RoomPeersList newInstance(String param1, String param2)
    {
        RoomPeersList fragment = new RoomPeersList();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public RoomPeersList()
    {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        this.setListAdapter(new ConnectedPeersAdapter(getActivity(), R.layout.row_devices, peers));
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_room_peers_list, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri)
    {
        if (mListener != null)
        {
            mListener.onFragmentInteraction(uri);
        }
    }


    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener
    {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
    }

    public void updateRoomPeers()
    {
        Toast.makeText(getActivity(), "Updating Peers List", Toast.LENGTH_LONG).show();

        if (MeshNetworkManager.routingTable !=  null || MeshNetworkManager.routingTable.values().size() <= 0)
        {
            return;
        }

        for (AllEncompasingP2PClient cPeer : MeshNetworkManager.routingTable.values())
        {
            if (cPeer != null)
            {
                peers.add(cPeer);
            }
        }
        ((ConnectedPeersAdapter) getListAdapter()).notifyDataSetChanged();
    }

    private class ConnectedPeersAdapter extends ArrayAdapter<AllEncompasingP2PClient>
    {
        private List<AllEncompasingP2PClient> connectedPeers;

        public ConnectedPeersAdapter(Context context, int textViewResourceId, List<AllEncompasingP2PClient> objects)
        {
            super(context, textViewResourceId, objects);
            connectedPeers = objects;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup partent)
        {
            View v = convertView;
            if (v == null)
            {
                LayoutInflater vi = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.row_devices, null);
            }

            AllEncompasingP2PClient peer = connectedPeers.get(position);
            if (peer != null)
            {
                TextView top = (TextView) v.findViewById(R.id.device_name);
                TextView bottom = (TextView) v.findViewById(R.id.device_details);
                if (top != null)
                {
                    top.setText(peer.getName());
                }
                if (bottom != null)
                {
                    bottom.setText(peer.getMac());
                }
            }
            return v;
        }
    }

}
