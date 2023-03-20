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

fig.add_trace(go.Scatter(name=systemType[0], x=[2, 10, 50, 100, 500, 1000], mode='lines+markers'
                         , y=[2836653.57, 1065865.22, 271424.121, 161894.28, 58156.157, 48988.25688]
                         , line=dict(color=config.central, width=5), marker=dict(size=20, symbol='circle')))
fig.add_trace(go.Scatter(name=systemType[1], x=[2, 10, 50, 100, 500, 1000], mode='lines+markers'
                         , y=[16482342.52, 3848162.886, 698074.5, 310295.056, 63150.8959, 30287.43]
                         , line=dict(color=config.desisIC, width=5), marker=dict(size=20, symbol='square')))
fig.add_trace(go.Scatter(name=systemType[2], x=[2, 10, 50, 100, 500, 1000], mode='lines+markers'
                         , y=[17417488.2, 16641848.84, 17059125.4, 17041621.84, 17065781.96, 16988246.22]
                         , line=dict(color=config.desisSW, width=5), marker=dict(size=20, symbol='triangle-up')))
fig.add_trace(go.Scatter(name=systemType[3], x=[2, 10, 50, 100, 500, 1000], mode='lines+markers'
                         , y=[31188430.3, 31832224.16, 31953790.69, 31577103.27, 31809065.76, 31680790.5]
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
        title_text="concurrent windows",
        titlefont=dict(size=35),
        ticktext=["2","10", '10<sup>2<sup>', "10<sup>3<sup>"],
        tickvals=[2, 10, 100, 1000],
        range=[0.3,3.1],
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
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2\shareFunction\/avgsum.pdf")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s2\shareFunction\/avgsum.svg")