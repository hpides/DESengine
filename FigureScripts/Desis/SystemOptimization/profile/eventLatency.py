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
                         , y=[41371.242, 86992.64, 973061.2, 7302282, 15060142, 75446896, 150664672, 1971787780, 36659253000]
                         , line=dict(color=config.central, width=5), marker=dict(size=20, symbol='circle')))
fig.add_trace(go.Scatter(name=systemType[1], x=[1, 2, 10, 50, 100, 500, 1000, 10000, 100000], mode='lines+markers'
                         , y=[594.4374, 633.06587, 659.0762, 627.77725, 635.65155, 697.626, 664.0254, 702.246, 752.414]
                         , line=dict(color=config.desisIC, width=5), marker=dict(size=20, symbol='square')))
fig.add_trace(go.Scatter(name=systemType[2], x=[1, 2, 10, 50, 100, 500, 1000, 10000, 100000], mode='lines+markers'
                         , y=[18728.057, 18989.793, 18729.848, 17027.867, 19617.309, 20284.566, 23297.809, 26522.008, 30655.375]
                         , line=dict(color=config.desisSW, width=7), marker=dict(size=25, symbol='triangle-up')))
fig.add_trace(go.Scatter(name=systemType[3], x=[1, 2, 10, 50, 100, 500, 1000, 10000, 100000], mode='lines+markers'
                         , y=[16435.846, 16800.053, 18710.41, 19588.287, 20031.568, 19656.146, 22377.71, 25042.482, 26296.23]
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
        x=-0.2,
        # bordercolor="Black",
        # borderwidth=2,
        # bgcolor="white",
        font=dict(
            size=30,
            color="black"
        ),
    ),
    yaxis=dict(
        title_text="Latency in ns",
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
        title_text="Events in Slice",
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
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2\profile\/eventLatency.pdf")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2\profile\/eventLatency.svg")