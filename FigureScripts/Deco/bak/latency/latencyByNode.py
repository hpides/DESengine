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

fig.add_trace(go.Bar(name="Local", x=["Central", "Deco"], y=[0, 36.15317]
                     , legendrank=1, width=0.6
                     # , text="<b>2.98GB<b>", textposition='outside', textfont = dict(size = 25)
                     , marker_color=config.central))
fig.add_trace(go.Bar(name="Root", x=["Central", "Deco"], y=[51.39076,  7.56443]
                     , legendrank=2, width=0.6
                     # , text=["<b>2.98GB<b>","<b>2.98GB<b>","<b>6.62MB<b>","<b>2.27KB<b>"], textposition='outside', textfont = dict(size = 25)
                     , marker_color=config.disco))



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
        ticktext=["0", "10ms", "20ms", "30ms" , "40ms", "50ms", "60ms",],
        tickvals=[0, 10, 20, 30, 40, 50, 60],
        tickmode="array",
        range=[0, 60.1],
        ticks="inside",
        ticklen=20,
        tickwidth=5,
    ),
    xaxis=dict(
        # title_text="network height",
        # titlefont=dict(size=35),
        ticktext=["Central", "Deco"],
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
fig.update_layout(barmode='stack', bargap=0.15, bargroupgap=0.0)

fig.update_xaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 , tickfont=dict(size=25))
fig.update_yaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 , tickfont=dict(size=35))
# fig.update_yaxes(showgrid=True, gridwidth=1, gridcolor='rgb(120,120,120)')


fig.show()
# if not os.path.exists("E:\On going Paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\latency"):
#     os.mkdir("E:\On going Paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\latency")
# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\On going Paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\latency\latencyByNode.pdf")
pio.write_image(fig, "E:\On going Paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\latency\latencyByNode.svg")