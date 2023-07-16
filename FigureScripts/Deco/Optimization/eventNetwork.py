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

fig.add_trace(go.Scatter(name=systemType[0], x=[0.1, 1, 5, 10, 50, 100], mode='lines+markers'
                         , y=[1024*1024*1024*2.164840698242188e-6*2, 1024*1024*1024*2.164840698242188e-6*2, 1024*1024*1024*2.164840698242188e-6*2, 1024*1024*1024*2.164840698242188e-6*2, 1024*1024*1024*2.164840698242188e-6*2, 1024*1024*1024*2.164840698242188e-6*2]
                         , line=dict(dash='dash', color=config.debuffer, width=5), marker=dict(size=20, symbol='circle')))
fig.add_trace(go.Scatter(name=systemType[1], x=[0.1, 1, 5, 10, 50, 100], mode='lines+markers'
                         , y=[3.5*1024*1024*1024*2.164840698242188e-6*2, 3.5*1024*1024*1024*2.164840698242188e-6*2, 3.5*1024*1024*1024*2.164840698242188e-6*2, 3.5*1024*1024*1024*2.164840698242188e-6*2, 3.5*1024*1024*1024*2.164840698242188e-6*2, 3.5*1024*1024*1024*2.164840698242188e-6*2]
                         , line=dict(dash='dashdot', color=config.deco, width=5), marker=dict(size=20, symbol='square')))
fig.add_trace(go.Scatter(name=systemType[2],x=[0.1, 1, 5, 10, 50, 100], mode='lines+markers'
                         , y=[1024*1024*1024*3.07464/1024*2, 1024*1024*1024*30.7464/1024*2, 1024*1024*1024*153.732/1024*2, 1024*1024*1024*307.464/1024*2, 1024*1024*1024*1.49*2, 1024*1024*1024*1.49*2]
                         , line=dict(dash='dot', color=config.decosy, width=5), marker=dict(size=35, symbol='triangle-up')))
fig.add_trace(go.Scatter(name=systemType[3], x=[0.1, 1, 5, 10, 50, 100], mode='lines+markers'
                         , y=[1024*1024*1024*3.07464/1024*2, 1024*1024*1024*30.7464/1024*2, 1024*1024*1024*153.732/1024*2, 1024*1024*1024*307.464/1024*2, 1024*1024*1024*1.49*2, 1024*1024*1024*1.49*2]
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
        title_text="bytes sent",
        titlefont=dict(size=35),
        ticktext=["0", "KB", "MB", "GB"],
        tickvals=[0, 1000, 1000000, 1000000000],
        range=[0, 11],
        type="log",
        ticks="inside",
        ticklen=20,
        tickwidth=5,
        tickfont=dict(size=35),
        # ticks="inside",
        # ticklen=20,
        # tickwidth =5,
    ),
    xaxis=dict(
        title_text="Event Rate Change(%)",
        titlefont=dict(size=35),
        ticktext=["0.1","1", '10', "10<sup>2<sup>"],
        tickvals=[0.1, 1, 10, 100],
        range=[-1,2.1],
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

fig.update_yaxes(showgrid=True, gridwidth=1, gridcolor='rgb(120,120,120)')


fig.update_xaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 , tickfont=dict(size=30))
fig.update_yaxes(showline=True, linewidth=3, linecolor='black'#, mirror=True
                 , tickfont=dict(size=35))


fig.show()
# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\On going Paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\s2\/eventrate\/network.pdf")
pio.write_image(fig, "E:\On going Paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\s2\/eventrate\/network.svg")