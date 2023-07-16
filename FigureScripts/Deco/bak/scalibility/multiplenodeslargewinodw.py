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

fig.add_trace(go.Scatter(name="Central", x=[100000, 200000, 500000, 1000000, 5000000, 10000000], mode='lines+markers'
                         , y=[2098916, 2073237.245, 2088468.03, 2194990, 2046141.96, 2081877.572]
                         , line=dict(color=config.central, width=5), marker=dict(size=20, symbol='circle')))
fig.add_trace(go.Scatter(name="Deco", x=[100000, 200000, 500000, 1000000, 5000000, 10000000], mode='lines+markers'
                         , y=[2248293, 4174489.07, 4214096.62, 4234068.64, 4462675.8, 4489876]
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
        ticktext=["0", "1M", "2M", "3M", "4M", "5M"],
        tickvals=[0, 1000000, 2000000, 3000000, 4000000, 5000000],
        tickmode="array",
        range=[0, 5100000],
        tickfont=dict(size=35),
        # ticks="inside",
        # ticklen=20,
        # tickwidth =5,
    ),
    xaxis=dict(
        title_text="window size",
        titlefont=dict(size=35),
        ticktext=['10<sup>5<sup>', "10<sup>6<sup>", "10<sup>7<sup>"],
        tickvals=[100000, 1000000, 10000000],
        range=[5,7.1],
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
if not os.path.exists("E:\my paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\/scalability"):
    os.mkdir("E:\my paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\/scalability")
# fig.write_image("images/fig1.svg")
pio.write_image(fig, "E:\my paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\/scalability\Throughputlargewindow.pdf")
pio.write_image(fig, "E:\my paper\Deco Efficient Decentralized Aggregation for Count-Based Windows\experiment\/scalability\Throughputlargewindow.svg")