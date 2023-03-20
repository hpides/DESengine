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
                         , y=[943521.25, 2281066.2, 15237810, 76937536, 158051888, 838387710, 2015908480, 33975343100, 331601628229]
                         , line=dict(color=config.central, width=5), marker=dict(size=20, symbol='circle')))
fig.add_trace(go.Scatter(name=systemType[1], x=[1, 2, 10, 50, 100, 500, 1000, 10000, 100000], mode='lines+markers'
                         , y=[659.53796, 676.7251, 687.9438, 671.0625, 688.483, 675.463, 657.223, 665.72, 685.2]
                         , line=dict(color=config.desisIC, width=5), marker=dict(size=20, symbol='square')))
fig.add_trace(go.Scatter(name=systemType[2], x=[1, 2, 10, 50, 100, 500, 1000, 10000, 100000], mode='lines+markers'
                         , y=[687.87854, 749.3745, 1269.3042, 1376.0103, 4194.469, 22488.312, 28665.893, 391615.72, 5422617.5]
                         , line=dict(color=config.desisSW, width=7), marker=dict(size=25, symbol='triangle-up')))
fig.add_trace(go.Scatter(name=systemType[3], x=[1, 2, 10, 50, 100, 500, 1000, 10000, 100000], mode='lines+markers'
                         , y=[619.221, 711.4099, 1274.1307, 1320.2651, 3852.5498, 18913.424, 25772.383, 368805.44, 5182138]
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
        title_text="latency in ns",
        titlefont=dict(size=35),
        ticktext=["1", "10<sup>3 <sup> ", '10<sup>6 <sup> ', "10<sup>9 <sup> ", "10<sup>12<sup>"],
        tickvals=[1, 1000, 1000000, 1000000000, 1000000000000],
        range=[0,12.5],
        type="log",
        ticks = "inside",
        ticklen = 20,
        tickwidth = 5,
        tickfont=dict(size=35),
    ),
    xaxis=dict(
        title_text="slices in window",
        titlefont=dict(size=35),
        ticktext=["1", "10", '10<sup>2<sup>', "10<sup>3<sup>", "10<sup>4<sup>" , "10<sup>5<sup>"],
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
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2\profile\/slicesLatency.pdf")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2\profile\/slicesLatency.svg")