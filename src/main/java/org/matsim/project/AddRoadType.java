package org.matsim.project;

import org.matsim.api.core.v01.network.Link;
import org.matsim.api.core.v01.network.Network;
import org.matsim.api.core.v01.network.NetworkWriter;
import org.matsim.core.network.NetworkUtils;
import org.matsim.core.network.io.MatsimNetworkReader;

public class AddRoadType {
    public void run(String	inputNetworkFilename,	String	outputNetworkFilename) {
        Network network	=	NetworkUtils.createNetwork();
        new MatsimNetworkReader(network).readFile(inputNetworkFilename);
        for	(Link link	:	network.getLinks().values())	{
            if	(link.getFreespeed()	>=	(80	/	3.6))	{
                link.getAttributes().putAttribute("hbefa_road_type",	"URB/MW-City/80");
            }	else if(link.getFreespeed() >= (60/ 3.6)&& link.getFreespeed()<(80/3.6))	{
                link.getAttributes().putAttribute("hbefa_road_type",	"URB/Trunk-City/60");	//	TODO	adapt	to	real	hbefa	values
            }
            else if(link.getFreespeed()>=(50/3.6)&&link.getFreespeed()<(60/3.6)){
                link.getAttributes().putAttribute("hbefa_road_type",	"URB/Local/50");
            }
            else if(link.getFreespeed()<(50/3.6)){
                link.getAttributes().putAttribute("hbefa_road_type",	"URB/Access/30");
            }
        }
        new NetworkWriter(network).write(outputNetworkFilename);
    }
    public static void main(String[]	args) {
        new	AddRoadType().run(
                "Scenarios/BilbaoTimeVariantNetwork/input/bilbao_multimodalnetwork.xml",
                "Scenarios/BilbaoTimeVariantNetwork/inputEmissions/network.xml"
        );
    }
}
