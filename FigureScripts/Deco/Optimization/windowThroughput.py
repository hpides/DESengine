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

systemType = ["Approx", "Deco<sub>monit</sub>", "Deco<sub>sync</sub>", "Deco<sub>async</sub>"]
fig = go.Figure()

fig.add_trace(go.Scatter(name=systemType[0], x=[1000, 10000, 100000, 1000000, 10000000, 100000000], mode='lines+markers'
                         , y=[11936321.96*2, 11936321.96*2, 11936321.96*2, 11936321.96*2, 11936321.96*2, 11936321.96*2]
                         , line=dict(dash='dash', color=config.debuffer, width=5), marker=dict(size=20, symbol='circle')))
fig.add_trace(go.Scatter(name=systemType[1], x=[1000, 10000, 100000, 1000000, 10000000, 100000000], mode='lines+markers'
                         , y=[7106.07*2, 71660.36527*2, 651332.26*2, 4138029.47*2, 9112212.6*2, 9148405.504*2]
                         , line=dict(dash='dashdot', color=config.deco, width=5), marker=dict(size=20, symbol='square')))
fig.add_trace(go.Scatter(name=systemType[2],x=[1000, 10000, 100000, 1000000, 10000000, 100000000], mode='lines+markers'
                         , y=[114896.76*2, 131987.076*2, 1259139.378*2, 5579072.098*2, 9135418.6*2, 9238807.6*2]
                         , line=dict(dash='dot', color=config.decosy, width=5), marker=dict(size=20, symbol='triangle-up')))
fig.add_trace(go.Scatter(name=systemType[3], x=[1000, 10000, 100000, 1000000, 10000000, 100000000], mode='lines+markers'
                         , y=[15895.53*2, 1526051.66*2, 9136779.975*2, 9117314.833*2, 9234489*2, 9121369.109*2,]
                         , line=dict(color=config.decoasy, width=5), marker=dict(size=20, symbol='cross')))

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
        x=-0.1,
        # bordercolor="Black",
        # borderwidth=2,
        # bgcolor="white",
        font=dict(
            size=25,
            color="black"
        ),
    ),
    yaxis=dict(
        title_text="Events/sec",
        titlefont=dict(size=35),
        exponentformat="e",
        ticktext=["0", "4M", "8M", "12M", "16M", "20M", "24M"],
        tickvals=[0, 4000000, 8000000, 12000000, 16000000, 20000000, 24000000],
        tickmode="array",
        range=[0, 25000000],
        tickfont=dict(size=35),
        # ticks="inside",
        # ticklen=20,
        # tickwidth =5,
    ),
    xaxis=dict(
        title_text="Window size (events)",
        titlefont=dict(size=35),
        ticktext=["10<sup>3<sup>", "10<sup>4<sup>","10<sup>5<sup>", "10<sup>6<sup>","10<sup>7<sup>", "10<sup>8<sup>"],
        tickvals=[1000, 10000, 100000, 1000000, 10000000, 100000000],
        range=[3,8.1],
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
                 , tickfont=dict(size=30))
fig.update_yaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 , tickfont=dict(size=35))

fig.update_yaxes(showgrid=True, gridwidth=1, gridcolor='rgb(120,120,120)')
fig.show()
fig.show()
# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\On going Paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\s2\/eventrate\/winthroughput.pdf")
pio.write_image(fig, "E:\On going Paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\s2\/eventrate\/winthroughput.svg")