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

fig.add_trace(go.Scatter(name="Central", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[2068487.831, 2070697.149, 2091610.9, 2095282.32, 2108962.22, 2111921.619, 2098082.798, 2046734.899]
                         , line=dict(color=config.central, width=5), marker=dict(size=20, symbol='circle')))

fig.add_trace(go.Scatter(name="Deco", x=[1, 2, 3, 4, 5, 6, 7, 8], mode='lines+markers'
                         , y=[2280188.373, 4269195.02, 6454876.194, 8874730.62, 10635030.54, 12617631.44, 14242142.35, 16005949.61]
                         , line=dict(color=config.desis, width=5), marker=dict(size=20, symbol='cross')))



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
        ticktext=["0", "40M", "80M", "120M", "160M"],
        tickvals=[0, 4000000, 8000000, 12000000, 16000000],
        tickmode="array",
        range=[0, 16500000],
        tickfont=dict(size=35),
        # ticks="inside",
        # ticklen=20,
        # tickwidth =5,
    ),

    xaxis=dict(
        title_text="local nodes",
        titlefont=dict(size=35),
        ticktext=["1", "2", "3", "4", "5", "6", "7", "8"],
        tickvals=[1, 2, 3, 4, 5, 6, 7, 8],
        range=[1,8.2],
        tickmode="array",
        ticks="inside",
        ticklen=20,
        tickwidth=5,
        tickfont=dict(size=35),
    )
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
if not os.path.exists("E:\my paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\/scalability"):
    os.mkdir("E:\my paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\/scalability")
# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\my paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\/scalability\ThroughputAverageMode.pdf")
pio.write_image(fig, "E:\my paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\/scalability\ThroughputAverageMode.svg")