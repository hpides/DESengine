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

systemType = ["CeBuffer", "DeBucket", "DeSW", "Desis"]
fig = go.Figure()

fig.add_trace(go.Scatter(name=systemType[0], x=[1, 2, 10, 50, 100, 500, 1000, 10000, 100000], mode='lines+markers'
                         , y=[4895917.833, 4489956.676, 3833151.633, 3788050.583, 3613234.948, 2730030.898, 2191024.004, 831013.9165, 304705.1027]
                         , line=dict(color=config.central, width=5), marker=dict(size=20, symbol='circle')))
fig.add_trace(go.Scatter(name=systemType[1], x=[1, 2, 10, 50, 100, 500, 1000, 10000, 100000], mode='lines+markers'
                         , y=[25391640.62, 24137930.16, 23819390.76, 23921398.49, 24847920.6, 25095030.31, 25608779.33, 32536461.6, 33835230.63]
                         , line=dict(color=config.desisIC, width=5), marker=dict(size=20, symbol='square')))
fig.add_trace(go.Scatter(name=systemType[2], x=[1, 2, 10, 50, 100, 500, 1000, 10000, 100000], mode='lines+markers'
                         , y=[24882246.39, 23880238.74, 22847564.43, 22639310.53, 22964915.27, 22907764.34, 22762613.3, 18710217.04, 8353758.063]
                         , line=dict(color=config.desisSW, width=5), marker=dict(size=20, symbol='triangle-up')))
fig.add_trace(go.Scatter(name=systemType[3], x=[1, 2, 10, 50, 100, 500, 1000, 10000, 100000], mode='lines+markers'
                         , y=[25336452.44, 25769423.97, 24697153.17, 25036862.25, 24123968.71, 24548169.73, 25131586.25, 19322268.49, 8372330.545]
                         , line=dict(color=config.desis, width=5), marker=dict(size=20, symbol='cross')))

# fig.update_traces(marker_color='rgb(158,202,225)', marker_line_color='rgb(8,48,107)', marker_line_width=1.5, opacity=0.6,
# marker=dict(size=10, symbol='triangle-up'))))

# fig.add_trace(go.Scatter(xaxis='x2'))
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
            size=23,
            color="black"
        ),
    ),
    yaxis=dict(
        title_text="events/sec",
        titlefont=dict(size=35),
        exponentformat="e",
        ticktext=["0", "5M", "10M", "15M", "20M", "25M", "30M", "35M"],
        tickvals=[0, 5000000, 10000000, 15000000, 20000000, 25000000, 30000000, 35000000],
        tickmode="array",
        range=[0, 36000000],
        tickfont=dict(size=35),
        # ticks="inside",
        # ticklen=20,
        # tickwidth =5,
    ),
    xaxis=dict(
        title_text="slices in window",
        titlefont=dict(size=35),
        ticktext=["1","10", '10<sup>2<sup>', "10<sup>3<sup>", "10<sup>4<sup>" , "10<sup>5<sup>"],
        tickvals=[1, 10, 100, 1000, 10000, 100000],
        range=[0,5.1],
        type="log",
        ticks = "inside",
        ticklen = 20,
        tickwidth = 5,
        tickfont=dict(size=35),
    ),
    # xaxis2=dict(
    #     ticktext=["50", "500"],
    #     tickvals=[50, 500],
    #     range=[0,3],
    #     type="log",
    #     ticks="inside",
    #     ticklen=20,
    #     tickwidth=5,
    #     overlaying="x",
    #     side="bottom",
    #     tickfont=dict(size=15),
    # )
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
fig.update_xaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 )
fig.update_yaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 )
fig.update_yaxes(showgrid=True, gridwidth=1, gridcolor='rgb(120,120,120)')
fig.show()
if not os.path.exists("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2"):
    os.mkdir("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2")

# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2\profile\/slicesThroughput.pdf")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2\profile\/slicesThroughput.svg")