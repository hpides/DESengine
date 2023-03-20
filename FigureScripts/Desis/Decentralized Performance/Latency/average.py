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



windowType = ['CeBuffer', 'Scotty', "Disco", "Desis"]
widthSin = 0.8
widthAll=[widthSin]

fig = go.Figure()

fig.add_trace(go.Bar(name="local", x=windowType, y=[0, 0, 341152, 137256]
                     , legendrank=1, width=widthAll
                     # , text="<b>2.98GB<b>", textposition='outside', textfont = dict(size = 25)
                     , marker_color=config.central))
fig.add_trace(go.Bar(name="intermediate", x=windowType, y=[0, 0, 387587, 71705]
                     , legendrank=2, width=widthAll
                     , marker_color=config.disco))
fig.add_trace(go.Bar(name="root", x=windowType, y=[2000000, 459361, 227278, 108941]
                     , legendrank=2, width=widthAll
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
        title_text="latency in ms",
        titlefont=dict(size=35),
        ticks="inside",
        ticklen=20,
        tickwidth=5,

        ticktext=["0.2", "0.4", "0.6", "0.8", "1.0", "...", "2.5"],
        tickvals=[200000, 400000, 600000, 800000, 1000000, 1200000, 1400000],
        tickmode="array",
        range=[0, 1410000],


        # ticktext=["10<sup>1<sup>", "10<sup>2<sup>", "10<sup>3<sup>", "10<sup>6<sup>", "10<sup>9<sup>"],
        # tickvals=[1, 100, 1000, 1000000,1000000000],
        # range=[0, 9],
        # type="log",

    ),
    xaxis=dict(
        # title_text="local nodes",
        # titlefont=dict(size=15),
        ticktext=['central', 'Scotty', "Disco", "Desis"],
        # tickvals=[1, 2, 3, 4],
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
fig.update_layout(barmode='stack', bargap=0.15, bargroupgap=0.0)

fig.update_xaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 , tickfont=dict(size=25))
fig.update_yaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 , tickfont=dict(size=35))
# fig.update_yaxes(showgrid=True, gridwidth=1, gridcolor='rgb(120,120,120)')

fig.show()
if not os.path.exists("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1"):
    os.mkdir("E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1")
# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1\/latency\/latencya.pdf")
pio.write_image(fig, "E:\my paper\DesisPaper\Desis-Optimizing-Decentralized-Window-Aggregation\experiment\s1\/latency\/latencya.svg")