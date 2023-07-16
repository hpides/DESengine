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

fig.add_trace(go.Bar(name="Central", x=[2, 3, 4, 5], y=[2.98, 5.96, 8.94, 11.92]
                     , legendrank=1, width=0.4
                     , text=["<b>2.98GB<b>","<b>5.96GB<b>","<b>8.94GB<b>","<b>11.9GB<b>"], textposition='outside', textfont = dict(size = 14)
                     , marker_color=config.central))
fig.add_trace(go.Bar(name="Deco", x=[2, 3, 4, 5], y=[11.89/1024, 23.78/1024, 35.67/1024, 47.56/1024]
                     , legendrank=2, width=0.4
                     , text=["<b>11.8MB<b>","<b>23.8MB<b>","<b>35.7MB<b>","<b>47.5MB<b>"], textposition='outside', textfont = dict(size = 14)
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
        ticktext=["0", "4GB", "8GB", "12GB", "16GB"],
        tickvals=[0, 4, 8, 12, 16],
        tickmode="array",
        range=[0, 16.1],
        ticks="inside",
        ticklen=20,
        tickwidth=5,
    ),
    xaxis=dict(
        title_text="network height",
        titlefont=dict(size=35),
        ticktext=["2", "3", "4", "5"],
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
pio.write_image(fig, "E:\my paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\/networkcost\moreHopsNetwork.pdf")
pio.write_image(fig, "E:\my paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\/networkcost\moreHopsNetwork.svg")