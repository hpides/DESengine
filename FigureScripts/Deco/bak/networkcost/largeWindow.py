import os
import plotly.graph_objects as go
import plotly.io as pio
import plotly.express as px

import os
import sys
# Append parent directory to import path
sys.path.insert(0, os.path.dirname(os.path.dirname(os.path.abspath(__file__))))
import config
pio.kaleido.scope.mathjax = None


fig = go.Figure()

fig.add_trace(go.Bar(name="Central", x=["100K", "1M", "10M"], y=[2.98, 2.98, 2.98, 2.98, 2.98, 2.98]
                     , legendrank=1, width=0.4
                     , text=["<b>2.98GB<b>","<b>2.98GB<b>","<b>2.98GB<b>","<b>2.98GB<b>"], textposition='outside', textfont = dict(size = 14)
                     , marker_color=config.central))
fig.add_trace(go.Bar(name="Deco", x=["100K", "1M", "10M"], y=[2.98,  11.72/1024, 11.85/1024, 11.89/1024, 12.326/1024, 12.378/1024]
                     , legendrank=2, width=0.4
                     , text=["<b>2.98GB<b>","<b>11.7MB<b>","<b>11.8MB<b>","<b>12.4MB<b>"], textposition='outside', textfont = dict(size = 14)
                     , marker_color=config.desis))



#legend
fig.update_layout(
    legend=dict(
        orientation="h",
        yanchor="top",
        y=1.2,
        xanchor="left",
        x=-0.05,
        # bordercolor="Black",
        # borderwidth=2,
        # bgcolor="white",
        font=dict(
            size=30,
            color="black"
        ),
    ),
    yaxis=dict(
        title_text="bytes sent by events",
        titlefont=dict(size=35),
        ticktext=["0", "1GB", "2GB", "3GB", "4GB"],
        tickvals=[0, 1, 2, 3, 4],
        tickmode="array",
        range=[0, 4.1],
        ticks="inside",
        ticklen=20,
        tickwidth=5,
    ),
    xaxis=dict(
        title_text="window size",
        titlefont=dict(size=35),
        ticktext=["100K", "1M", "10M"],
        #tickvals=[2, 3, 4, 5],
        tickmode="array",
        # range=[0, 6],
    ),
)

# size & color
fig.update_layout(
    autosize=False,
    width=660,
    height=440,
    paper_bgcolor='rgba(0,0,0,0)',
    plot_bgcolor='rgba(0,0,0,0)',
    margin=dict(
        l=5,
        r=5,
        b=5,
        t=5,
        pad=0
    ),
)
fig.update_layout(barmode='group', bargap=0.20, bargroupgap=0.0)

fig.update_xaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 , tickfont=dict(size=25))
fig.update_yaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 , tickfont=dict(size=35))
# fig.update_yaxes(showgrid=True, gridwidth=1, gridcolor='rgb(120,120,120)')
fig.show()
if not os.path.exists("E:\my paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\/networkcost"):
    os.mkdir("E:\my paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\/networkcost")
# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\my paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\/networkcost\largeWindowNetwork.pdf")
pio.write_image(fig, "E:\my paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\/networkcost\largeWindowNetwork.svg")